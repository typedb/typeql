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

use std::{collections::HashSet, fmt, iter};

use itertools::Itertools;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        Result,
        string::indent,
        token,
        validatable::Validatable,
    },
    pattern::{Label, VariablesRetrieved},
    query::{MatchClause, modifier::Modifiers, TypeQLGetAggregate},
    variable::{Variable, variable::VariableRef},
    write_joined,
};
use crate::common::identifier::is_valid_identifier;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLFetch {
    pub match_clause: MatchClause,
    pub projections: Vec<Projection>,
    pub modifiers: Modifiers,
}

impl TypeQLFetch {
    fn validate_names_are_unique(&self) -> Result {
        let all_refs = self
            .match_clause
            .retrieved_variables()
            .chain(self.projections.iter().flat_map(|p| p.key_variable().into_iter().chain(p.value_variables())));
        let (concept_refs, value_refs): (HashSet<VariableRef<'_>>, HashSet<VariableRef<'_>>) =
            all_refs.partition(|r| r.is_concept());
        let concept_names = concept_refs.iter().collect::<HashSet<_>>();
        let value_names = value_refs.iter().collect::<HashSet<_>>();
        let common_refs = concept_names.intersection(&value_names).collect::<HashSet<_>>();
        if !common_refs.is_empty() {
            return Err(TypeQLError::VariableNameConflict {
                names: common_refs.iter().map(|r| r.to_string()).join(", "),
            }
            .into());
        }
        Ok(())
    }
}

impl Validatable for TypeQLFetch {
    fn validate(&self) -> Result {
        let match_variables = self.match_clause.retrieved_variables().collect();
        collect_err([
            self.match_clause.validate(),
            self.modifiers.sorting.as_ref().map_or(Ok(()), |s| s.validate(&match_variables)),
            self.validate_names_are_unique(),
        ])
    }
}

impl VariablesRetrieved for TypeQLFetch {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            self.match_clause.retrieved_variables().chain(self.projections.iter().flat_map(Projection::key_variable)),
        )
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum Projection {
    Variable(ProjectionKeyVar),
    Attribute(ProjectionKeyVar, Vec<ProjectionAttribute>),
    Subquery(ProjectionKeyLabel, ProjectionSubquery),
}

impl Projection {
    pub fn key_variable(&self) -> Option<VariableRef<'_>> {
        match self {
            Projection::Variable(key) | Projection::Attribute(key, _) => Some(key.variable.as_ref()),
            Projection::Subquery(_, _) => None,
        }
    }

    pub fn value_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            Projection::Variable(_) | Projection::Attribute(_, _) => Box::new(iter::empty()),
            Projection::Subquery(_, subquery) => subquery.variables(),
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct ProjectionKeyVar {
    pub(crate) variable: Variable,
    pub(crate) label: Option<ProjectionKeyLabel>,
}

impl ProjectionKeyVar {
    pub fn label(self, label: impl Into<ProjectionKeyLabel>) -> Self {
        ProjectionKeyVar { label: Some(label.into()), ..self }
    }
}

impl<T: Into<ProjectionKeyVar>> From<T> for Projection {
    fn from(key_var: T) -> Self {
        Projection::Variable(key_var.into())
    }
}

pub trait ProjectionKeyVarBuilder {
    fn label(self, label: impl Into<ProjectionKeyLabel>) -> ProjectionKeyVar;
}

impl<T: Into<Variable>> ProjectionKeyVarBuilder for T {
    fn label(self, label: impl Into<ProjectionKeyLabel>) -> ProjectionKeyVar {
        let labeled = label.into();
        ProjectionKeyVar { variable: self.into(), label: Some(labeled) }
    }
}

impl<T: Into<Variable>, U: Into<ProjectionKeyLabel>> From<(T, U)> for ProjectionKeyVar {
    fn from((var, label): (T, U)) -> Self {
        ProjectionKeyVar { variable: var.into(), label: Some(label.into()) }
    }
}

impl<T: Into<Variable>> From<T> for ProjectionKeyVar {
    fn from(var: T) -> Self {
        ProjectionKeyVar { variable: var.into(), label: None }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct ProjectionKeyLabel {
    pub label: String,
}

impl ProjectionKeyLabel {
    pub fn map_subquery_get_aggregate(self, subquery: TypeQLGetAggregate) -> Projection {
        Projection::Subquery(self, ProjectionSubquery::GetAggregate(subquery))
    }

    pub fn map_subquery_fetch(self, subquery: TypeQLFetch) -> Projection {
        Projection::Subquery(self, ProjectionSubquery::Fetch(Box::new(subquery)))
    }

    fn must_quote(s: &str) -> bool {
        !is_valid_identifier(s)
    }
}

impl From<&str> for ProjectionKeyLabel {
    fn from(value: &str) -> Self {
        Self::from(value.to_owned())
    }
}

impl From<String> for ProjectionKeyLabel {
    fn from(label: String) -> Self {
        Self { label }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct ProjectionAttribute {
    pub(crate) attribute: Label,
    pub(crate) label: Option<ProjectionKeyLabel>,
}

impl ProjectionAttribute {
    pub fn label(self, label: impl Into<ProjectionKeyLabel>) -> Self {
        ProjectionAttribute { label: Some(label.into()), ..self }
    }
}

impl From<&str> for ProjectionAttribute {
    fn from(attribute: &str) -> Self {
        Self::from(attribute.to_owned())
    }
}

impl From<String> for ProjectionAttribute {
    fn from(attribute: String) -> Self {
        ProjectionAttribute { attribute: Label::from(attribute), label: None }
    }
}

impl<T: Into<Label>, U: Into<ProjectionKeyLabel>> From<(T, U)> for ProjectionAttribute {
    fn from((attribute, label): (T, U)) -> Self {
        Self { attribute: attribute.into(), label: Some(label.into()) }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum ProjectionSubquery {
    GetAggregate(TypeQLGetAggregate),
    Fetch(Box<TypeQLFetch>),
}

impl ProjectionSubquery {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            ProjectionSubquery::GetAggregate(query) => query.query.retrieved_variables(),
            ProjectionSubquery::Fetch(query) => query.retrieved_variables(),
        }
    }
}

pub trait ProjectionBuilder {
    fn map_attribute(self, attribute: impl Into<ProjectionAttribute>) -> Projection;
    fn map_attributes(self, attribute: Vec<ProjectionAttribute>) -> Projection;
}

impl<T: Into<ProjectionKeyVar>> ProjectionBuilder for T {
    fn map_attribute(self, attribute: impl Into<ProjectionAttribute>) -> Projection {
        Projection::Attribute(self.into(), vec![attribute.into()])
    }

    fn map_attributes(self, attributes: Vec<ProjectionAttribute>) -> Projection {
        Projection::Attribute(self.into(), attributes)
    }
}

impl fmt::Display for TypeQLFetch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.match_clause)?;
        writeln!(f, "{}", token::Clause::Fetch)?;
        write_joined!(f, "\n", self.projections)?;
        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)
        } else {
            Ok(())
        }
    }
}

impl fmt::Display for Projection {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Projection::Variable(key) => {
                write!(f, "{};", key)
            }
            Projection::Attribute(key, attrs) => {
                write!(f, "{}: ", key)?;
                write_joined!(f, ", ", attrs)?;
                write!(f, ";")
            }
            Projection::Subquery(label, subquery) => {
                write!(f, "{}: {{\n{}\n}};", label, indent(subquery.to_string().as_ref()))
            }
        }
    }
}

impl fmt::Display for ProjectionKeyVar {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(label) = &self.label {
            write!(f, " {} {}", token::Projection::As, label)
        } else {
            Ok(())
        }
    }
}

impl fmt::Display for ProjectionKeyLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let label = &self.label;
        if Self::must_quote(label) {
            write!(f, "\"{}\"", label)
        } else {
            write!(f, "{}", label)
        }
    }
}

impl fmt::Display for ProjectionAttribute {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.attribute)?;
        if let Some(label) = &self.label {
            write!(f, " {} {}", token::Projection::As, label)
        } else {
            Ok(())
        }
    }
}

impl fmt::Display for ProjectionSubquery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ProjectionSubquery::GetAggregate(query) => {
                write!(f, "{}", query)
            }
            ProjectionSubquery::Fetch(query) => {
                write!(f, "{}", query)
            }
        }
    }
}
