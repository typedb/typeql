/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use std::{collections::HashSet, fmt, iter};

use itertools::Itertools;

use crate::{
    common::{
        error::{collect_err, Error, TypeQLError},
        Result,
        token,
        validatable::Validatable,
    },
    pattern::{Conjunction, NamedReferences, Pattern, Reference, UnboundVariable},
    query::{AggregateQueryBuilder, TypeQLDelete, TypeQLGetGroup, TypeQLInsert, Writable},
    write_joined,
};
use crate::query::MatchClause;
use crate::query::modifier::{Modifiers, Sorting};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLGet {
    pub clause_match: MatchClause,
    pub filter: Filter,
    pub modifiers: Modifiers,
}

impl AggregateQueryBuilder for TypeQLGet {}

impl TypeQLGet {
    pub fn filter(self, vars: Vec<UnboundVariable>) -> Self {
        TypeQLGet { filter: Filter { vars }, ..self }
    }

    pub fn get<const N: usize, T: Into<UnboundVariable>>(self, vars: [T; N]) -> Self {
        self.filter(vars.into_iter().map(|var| var.into()).collect::<Vec<_>>())
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        TypeQLGet { modifiers: self.modifiers.sort(sorting), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        TypeQLGet { modifiers: self.modifiers.limit(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        TypeQLGet { modifiers: self.modifiers.offset(offset), ..self }
    }

    pub fn insert(self, vars: impl Writable) -> TypeQLInsert {
        TypeQLInsert { clause_match: Some(self.clause_match), statements: vars.vars(), modifiers: self.modifiers }
    }

    pub fn delete(self, vars: impl Writable) -> TypeQLDelete {
        TypeQLDelete { clause_match: self.clause_match, statements: vars.vars(), modifiers: self.modifiers }
    }

    pub fn group(self, var: impl Into<UnboundVariable>) -> TypeQLGetGroup {
        TypeQLGetGroup { get_query: self, group_var: var.into() }
    }
}

impl Validatable for TypeQLGet {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut [
                expect_has_bounding_conjunction(&self.conjunction),
                expect_nested_patterns_are_bounded(&self.conjunction),
                expect_each_variable_is_bounded_by_named(self.conjunction.patterns.iter()),
                expect_filters_are_in_scope(&self.conjunction, &self.modifiers.filter),
                expect_sort_vars_are_in_scope(&self.conjunction, &self.modifiers.filter, &self.modifiers.sorting),
                expect_variable_names_are_unique(&self.conjunction),
            ]
                .into_iter()
                .chain(self.conjunction.patterns.iter().map(|p| p.validate())),
        )
    }
}

impl NamedReferences for TypeQLGet {
    fn named_references(&self) -> HashSet<Reference> {
        if let Some(filter) = &self.modifiers.filter {
            filter.vars.iter().map(|v| v.reference().clone()).collect()
        } else {
            self.conjunction.named_references()
        }
    }
}

fn expect_has_bounding_conjunction(conjunction: &Conjunction) -> Result<()> {
    if conjunction.has_named_variables() {
        Ok(())
    } else {
        Err(TypeQLError::MatchHasNoBoundingNamedVariable())?
    }
}

fn expect_nested_patterns_are_bounded(conjunction: &Conjunction) -> Result<()> {
    let bounds = conjunction.named_references();
    collect_err(&mut conjunction.patterns.iter().map(|p| p.expect_is_bounded_by(&bounds)))
}

fn expect_each_variable_is_bounded_by_named<'a>(patterns: impl Iterator<Item=&'a Pattern>) -> Result<()> {
    collect_err(&mut patterns.map(|p| {
        match p {
            Pattern::Variable(v) => v
                .references()
                .any(|r| r.is_name())
                .then_some(())
                .ok_or_else(|| Error::from(TypeQLError::MatchPatternVariableHasNoNamedVariable(p.clone()))),
            Pattern::Conjunction(c) => expect_each_variable_is_bounded_by_named(c.patterns.iter()),
            Pattern::Disjunction(d) => expect_each_variable_is_bounded_by_named(d.patterns.iter()),
            Pattern::Negation(n) => expect_each_variable_is_bounded_by_named(iter::once(n.pattern.as_ref())),
        }
    }))
}

fn expect_filters_are_in_scope(conjunction: &Conjunction, filter: &Option<Filter>) -> Result<()> {
    let names_in_scope = conjunction.named_references();
    let mut seen = HashSet::new();
    if filter.as_ref().map_or(false, |f| f.vars.is_empty()) {
        Err(TypeQLError::EmptyMatchFilter())?;
    }
    collect_err(&mut filter.iter().flat_map(|f| &f.vars).map(|v| v.reference()).map(|r| {
        if !r.is_name() {
            Err(TypeQLError::VariableNotNamed().into())
        } else if !names_in_scope.contains(r) {
            Err(TypeQLError::VariableOutOfScopeMatch(r.clone()).into())
        } else if seen.contains(&r) {
            Err(TypeQLError::IllegalFilterVariableRepeating(r.clone()).into())
        } else {
            seen.insert(r);
            Ok(())
        }
    }))
}

fn expect_sort_vars_are_in_scope(
    conjunction: &Conjunction,
    filter: &Option<Filter>,
    sorting: &Option<Sorting>,
) -> Result<()> {
    let names_in_scope = filter
        .as_ref()
        .map(|f| f.vars.iter().map(|v| v.reference().clone()).collect())
        .unwrap_or_else(|| conjunction.named_references());
    collect_err(&mut sorting.iter().flat_map(|s| &s.vars).map(|v| v.var.reference().clone()).map(|r| {
        names_in_scope.contains(&r).then_some(()).ok_or_else(|| TypeQLError::VariableOutOfScopeMatch(r).into())
    }))
}

fn expect_variable_names_are_unique(conjunction: &Conjunction) -> Result<()> {
    let all_refs = conjunction.references_recursive();
    let (concept_refs, value_refs): (HashSet<&Reference>, HashSet<&Reference>) = all_refs.partition(|r| r.is_concept());
    let concept_names = concept_refs.iter().map(|r| r.name()).collect::<HashSet<_>>();
    let value_names = value_refs.iter().map(|r| r.name()).collect::<HashSet<_>>();
    let common_refs = concept_names.intersection(&value_names).collect::<HashSet<_>>();
    if !common_refs.is_empty() {
        return Err(TypeQLError::VariableNameConflict(common_refs.iter().map(|r| r.to_string()).join(", ")).into());
    }
    Ok(())
}

impl fmt::Display for TypeQLGet {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.clause_match)?;
        write!(f, "\n{}", self.filter)?;
        write!(f, "\n{}", self.modifiers)?;
        Ok(())
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Filter {
    pub vars: Vec<UnboundVariable>,
}

impl fmt::Display for Filter {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Filter::Get)?;
        write_joined!(f, ", ", self.vars)
    }
}
