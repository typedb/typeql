/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    pretty::{indent, Pretty},
    query::Pipeline,
    type_::Type,
    util::write_joined,
    variable::Variable,
};

#[derive(Debug, Eq, PartialEq)]
pub struct Fetch {
    span: Option<Span>,
    projections: Vec<Projection>,
}

impl Fetch {
    pub fn new(span: Option<Span>, projections: Vec<Projection>) -> Self {
        Self { span, projections }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum Projection {
    Variable(ProjectionKeyVar),
    Attribute(ProjectionKeyVar, Vec<ProjectionAttribute>),
    Subquery(ProjectionKeyLabel, Pipeline),
}

#[derive(Debug, Eq, PartialEq)]
pub struct ProjectionKeyVar {
    variable: Variable,
    label: Option<ProjectionKeyLabel>,
}

impl ProjectionKeyVar {
    pub fn new(variable: Variable, label: Option<ProjectionKeyLabel>) -> Self {
        Self { variable, label }
    }

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
    attribute: Type,
    label: Option<ProjectionKeyLabel>,
}

impl ProjectionAttribute {
    pub fn new(attribute: Type, label: Option<ProjectionKeyLabel>) -> Self {
        Self { attribute, label }
    }

    pub fn label(self, label: impl Into<ProjectionKeyLabel>) -> Self {
        ProjectionAttribute { label: Some(label.into()), ..self }
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

impl Pretty for Fetch {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Fetch)?;
        for projection in &self.projections {
            writeln!(f)?;
            indent(indent_level, f)?;
            Pretty::fmt(projection, indent_level, f)?;
        }
        Ok(())
    }
}

impl fmt::Display for Fetch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            write!(f, "{} ", token::Clause::Fetch)?;
            write_joined!(f, " ", self.projections)?;
            Ok(())
        }
    }
}

impl Pretty for Projection {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
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
                writeln!(f, "{}: {{", label)?;
                Pretty::fmt(subquery, indent_level + 1, f)?;
                writeln!(f)?;
                indent(indent_level, f)?;
                write!(f, "}};")?;
                Ok(())
            }
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
                write!(f, "{}: {{ {} }};", label, subquery)
            }
        }
    }
}

impl fmt::Display for ProjectionKeyVar {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(label) = &self.label {
            write!(f, " {} {}", token::Keyword::As, label)
        } else {
            Ok(())
        }
    }
}

impl fmt::Display for ProjectionKeyLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let label = &self.label;
        write!(f, "{}", label)
    }
}

impl fmt::Display for ProjectionAttribute {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.attribute)?;
        if let Some(label) = &self.label {
            write!(f, " {} {}", token::Keyword::As, label)
        } else {
            Ok(())
        }
    }
}
