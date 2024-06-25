/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashMap, fmt};

use self::{
    thing::{AttributeComparisonStatement, AttributeValueStatement, RelationStatement, ThingStatement},
    type_::{LabelConstraint, Owns, Plays, Relates, Sub, ValueType},
};
use crate::{
    common::{
        token::{self},
        Span,
    },
    expression::{Expression, FunctionCall},
    identifier::{Label, ScopedLabel, Variable},
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

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct BuiltinValueType {
    span: Option<Span>,
    name: token::ValueType,
}

impl BuiltinValueType {
    pub fn new(span: Option<Span>, name: token::ValueType) -> Self {
        Self { span, name }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Type {
    Optional(Box<Type>),

    Label(Label),
    ScopedLabel(ScopedLabel),
    Variable(Variable),
    BuiltinValue(BuiltinValueType),

    ListLabel(Label),
    ListVariable(Variable),
    ListBuiltinValue(BuiltinValueType),
}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Optional(inner) => write!(f, "{inner}?"),
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::ScopedLabel(inner) => fmt::Display::fmt(inner, f),
            Self::Variable(inner) => todo!(),
            Self::BuiltinValue(inner) => todo!(),
            Self::ListLabel(inner) => todo!(),
            Self::ListVariable(inner) => todo!(),
            Self::ListBuiltinValue(inner) => todo!(),
        }
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

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeConstraint {
    Sub(Sub),
    Label(LabelConstraint),
    ValueType(ValueType),
    Owns(Owns),
    Relates(Relates),
    Plays(Plays),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Is(Is),
    InStream(InStream),
    Comparison(comparison::ComparisonStatement),
    Assignment(Assignment),
    Thing(ThingStatement),
    Relation(RelationStatement),
    AttributeValue(AttributeValueStatement),
    AttributeComparison(AttributeComparisonStatement),
    Type(TypeStatement),
}
