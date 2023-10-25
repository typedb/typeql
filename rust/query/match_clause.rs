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
 */

use std::{fmt, iter};

use crate::common::{Error, token};
use crate::common::error::{collect_err, TypeQLError};
use crate::common::validatable::Validatable;
use crate::pattern::{Conjunction, Pattern, VariablesRetrieved};
use crate::query::{Projection, typeql_get, TypeQLDelete, TypeQLFetch, TypeQLGet, TypeQLInsert, Writable};
use crate::query::modifier::Modifiers;
use crate::Result;
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct MatchClause {
    pub conjunction: Conjunction,
}

impl MatchClause {
    pub fn new(conjunction: Conjunction) -> Self {
        Self { conjunction }
    }

    pub fn from_patterns(patterns: Vec<Pattern>) -> Self {
        Self::new(Conjunction::new(patterns))
    }

    pub fn get(self) -> TypeQLGet {
        TypeQLGet { clause_match: self, filter: typeql_get::Filter { vars: Vec::default() }, modifiers: Modifiers::default() }
    }

    pub fn get_vars(self, vars: Vec<Variable>) -> TypeQLGet {
        TypeQLGet { clause_match: self, filter: typeql_get::Filter { vars }, modifiers: Modifiers::default() }
    }

    pub fn get_fixed<const N: usize, T: Into<Variable>>(self, vars: [T; N]) -> TypeQLGet {
        self.get_vars(vars.into_iter().map(|var| var.into()).collect::<Vec<_>>())
    }

    pub fn fetch(self, projections: Vec<Projection>) -> TypeQLFetch {
        TypeQLFetch { clause_match: self, projections, modifiers: Modifiers::default() }
    }

    pub fn fetch_fixed<const N: usize>(self, projections: [Projection; N]) -> TypeQLFetch {
        self.fetch(projections.into_iter().collect::<Vec<_>>())
    }

    pub fn insert(self, writable: impl Writable) -> TypeQLInsert {
        TypeQLInsert { clause_match: Some(self), statements: writable.statements(), modifiers: Modifiers::default() }
    }

    pub fn delete(self, writable: impl Writable) -> TypeQLDelete {
        TypeQLDelete { clause_match: self, statements: writable.statements(), modifiers: Modifiers::default() }
    }

    fn validate_nested_patterns_are_bounded(&self) -> Result {
        let bounds = self.conjunction.retrieved_variables().collect();
        collect_err(self.conjunction.patterns.iter().map(|p| p.validate_is_bounded_by(&bounds)))
    }
}

impl VariablesRetrieved for MatchClause {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        self.conjunction.retrieved_variables()
    }
}

impl Validatable for MatchClause {
    fn validate(&self) -> Result {
        self.validate_nested_patterns_are_bounded()?;
        validate_statements_have_named_variable(self.conjunction.patterns.iter())?;
        collect_err(self.conjunction.patterns.iter().map(|p| p.validate()))
    }
}

fn validate_statements_have_named_variable<'a>(patterns: impl Iterator<Item=&'a Pattern>) -> Result {
    collect_err(patterns.map(|p| {
        match p {
            Pattern::Statement(v) => v
                .variables()
                .any(|r| r.is_name())
                .then_some(())
                .ok_or_else(|| Error::from(TypeQLError::MatchStatementHasNoNamedVariable(p.clone()))),
            Pattern::Conjunction(c) => validate_statements_have_named_variable(c.patterns.iter()),
            Pattern::Disjunction(d) => validate_statements_have_named_variable(d.patterns.iter()),
            Pattern::Negation(n) => validate_statements_have_named_variable(iter::once(n.pattern.as_ref())),
        }
    }))
}

impl fmt::Display for MatchClause {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Match)?;

        for pattern in &self.conjunction.patterns {
            write!(f, "\n{pattern};")?;
        }

        Ok(())
    }
}