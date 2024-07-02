/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{
    collections::HashMap,
    fmt::{self, Write},
};

use self::{
    comparison::ComparisonStatement,
    thing::{AttributeComparisonStatement, AttributeValueStatement, ThingStatement},
    type_::TypeConstraintBase,
};
use crate::{
    annotation::Annotation,
    common::{token, Span},
    expression::{Expression, FunctionCall},
    identifier::{Label, Variable},
    pretty::{indent, Pretty},
    type_::Type,
    util::write_joined,
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
pub struct TypeStatement {
    span: Option<Span>,
    type_: Type,
    constraints: Vec<TypeConstraint>,
}

impl TypeStatement {
    pub fn new(span: Option<Span>, type_: Type, constraints: Vec<TypeConstraint>) -> Self {
        Self { span, type_, constraints }
    }
}

impl Pretty for TypeStatement {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.type_)?;
        if let Some((first, rest)) = self.constraints.split_first() {
            f.write_char(' ')?;
            Pretty::fmt(first, indent_level, f)?;
            for constraint in rest {
                f.write_str(",\n")?;
                indent(indent_level + 1, f)?;
                Pretty::fmt(constraint, indent_level, f)?;
            }
        }
        Ok(())
    }
}

impl fmt::Display for TypeStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.type_)?;
        if let Some((first, rest)) = self.constraints.split_first() {
            write!(f, " {}", first)?;
            for constraint in rest {
                write!(f, ", {}", constraint)?;
            }
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeConstraint {
    span: Option<Span>,
    base: TypeConstraintBase,
    annotations: Vec<Annotation>,
}

impl TypeConstraint {
    pub fn new(span: Option<Span>, base: TypeConstraintBase, annotations: Vec<Annotation>) -> Self {
        Self { span, base, annotations }
    }
}

impl Pretty for TypeConstraint {}

impl fmt::Display for TypeConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.base)?;
        for annotation in &self.annotations {
            write!(f, " {}", annotation)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Is(Is),
    InStream(InStream),
    Comparison(ComparisonStatement),
    Assignment(Assignment),
    Thing(ThingStatement),
    AttributeValue(AttributeValueStatement),
    AttributeComparison(AttributeComparisonStatement),
    Type(TypeStatement),
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
