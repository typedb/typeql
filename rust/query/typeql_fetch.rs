/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt, iter};

use itertools::Itertools;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        identifier::is_valid_label_identifier,
        string::indent,
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Label, VariablesRetrieved},
    query::{modifier::Modifiers, MatchClause, TypeQLGetAggregate},
    write_joined,
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLFetch {
    pub match_clause: MatchClause,
    pub projections: Vec<Projection>,
    pub modifiers: Modifiers,
}

impl Validatable for TypeQLFetch {
    fn validate(&self) -> Result {
        let match_variables = self.match_clause.retrieved_variables().collect();
        collect_err([
            self.match_clause.validate(),
            self.modifiers.sorting.as_ref().map_or(Ok(()), |s| s.validate(&match_variables)),
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
        !is_valid_label_identifier(s)
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
