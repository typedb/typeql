/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashMap, fmt};

use self::{
    comparison::ComparisonStatement,
    thing::{AttributeComparisonStatement, AttributeValueStatement},
};
pub use self::{thing::Thing, type_::Type};
use crate::{
    common::{token, Span},
    expression::{Expression, FunctionCall},
    pretty::Pretty,
    type_::Label,
    util::write_joined,
    variable::Variable,
};

pub mod comparison;
pub mod thing;
pub mod type_;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Is {
    span: Option<Span>,
    lhs: Variable,
    rhs: Variable,
}

impl Is {
    pub(crate) fn new(span: Option<Span>, lhs: Variable, rhs: Variable) -> Self {
        Self { span, lhs, rhs }
    }
}

impl Pretty for Is {}

impl fmt::Display for Is {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.lhs, token::Keyword::Is, self.rhs)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct InStream {
    span: Option<Span>,
    lhs: Vec<Variable>,
    rhs: FunctionCall,
}

impl InStream {
    pub(crate) fn new(span: Option<Span>, lhs: Vec<Variable>, rhs: FunctionCall) -> Self {
        Self { span, lhs, rhs }
    }
}

impl Pretty for InStream {}

impl fmt::Display for InStream {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write_joined!(f, ", ", self.lhs)?;
        write!(f, " {} {}", token::Keyword::In, self.rhs)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum DeconstructField {
    Variable(Variable),
    Deconstruct(StructDeconstruct),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct StructDeconstruct {
    span: Option<Span>,
    field_map: HashMap<Label, DeconstructField>,
}

impl StructDeconstruct {
    pub fn new(span: Option<Span>, field_map: HashMap<Label, DeconstructField>) -> Self {
        Self { span, field_map }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AssignmentPattern {
    Variables(Vec<Variable>),
    Deconstruct(StructDeconstruct),
}

impl Pretty for AssignmentPattern {}

impl fmt::Display for AssignmentPattern {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variables(vars) => write_joined!(f, ", ", vars),
            Self::Deconstruct(_) => todo!(),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Assignment {
    span: Option<Span>,
    lhs: AssignmentPattern,
    rhs: Expression,
}

impl Assignment {
    pub fn new(span: Option<Span>, lhs: AssignmentPattern, rhs: Expression) -> Self {
        Self { span, lhs, rhs }
    }
}

impl Pretty for Assignment {}

impl fmt::Display for Assignment {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} = {}", self.lhs, self.rhs)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Is(Is),
    InStream(InStream),
    Comparison(ComparisonStatement),
    Assignment(Assignment),
    Thing(Thing),
    AttributeValue(AttributeValueStatement),
    AttributeComparison(AttributeComparisonStatement),
    Type(Type),
}

impl Pretty for Statement {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Statement::Is(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::InStream(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::Comparison(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::Assignment(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::Thing(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::AttributeValue(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::AttributeComparison(inner) => Pretty::fmt(inner, indent_level, f),
            Statement::Type(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Statement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Statement::Is(inner) => fmt::Display::fmt(inner, f),
            Statement::InStream(inner) => fmt::Display::fmt(inner, f),
            Statement::Comparison(inner) => fmt::Display::fmt(inner, f),
            Statement::Assignment(inner) => fmt::Display::fmt(inner, f),
            Statement::Thing(inner) => fmt::Display::fmt(inner, f),
            Statement::AttributeValue(inner) => fmt::Display::fmt(inner, f),
            Statement::AttributeComparison(inner) => fmt::Display::fmt(inner, f),
            Statement::Type(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
