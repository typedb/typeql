/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use self::isa::Isa;
use super::{comparison, Statement, Type, TypeAny};
use crate::{
    common::Span,
    expression::{Expression, Value},
    identifier::Variable,
    pattern::Pattern,
};

pub mod isa;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ThingStatement {
    span: Option<Span>,
    head: Variable,
    constraints: Vec<ThingConstraint>,
}

impl ThingStatement {
    pub fn new(span: Option<Span>, head: Variable, constraints: Vec<ThingConstraint>) -> Self {
        Self { span, head, constraints }
    }
}

impl From<ThingStatement> for Statement {
    fn from(val: ThingStatement) -> Self {
        Statement::Thing(val)
    }
}

impl From<ThingStatement> for Pattern {
    fn from(val: ThingStatement) -> Self {
        Pattern::Statement(Statement::Thing(val))
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum RolePlayer {
    Typed(TypeAny, Variable),
    Untyped(Variable),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relation {
    span: Option<Span>,
    role_players: Vec<RolePlayer>,
}

impl Relation {
    pub fn new(span: Option<Span>, role_players: Vec<RolePlayer>) -> Self {
        Self { span, role_players }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelationStatement {
    span: Option<Span>,
    head: Relation,
    constraints: Vec<ThingConstraint>,
}

impl RelationStatement {
    pub fn new(span: Option<Span>, head: Relation, constraints: Vec<ThingConstraint>) -> Self {
        Self { span, head, constraints }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct AttributeValueStatement {
    span: Option<Span>,
    type_: Option<Type>,
    value: Value,
    isa: Isa,
}

impl AttributeValueStatement {
    pub fn new(span: Option<Span>, type_: Option<Type>, value: Value, isa: Isa) -> Self {
        Self { span, type_, value, isa }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct AttributeComparisonStatement {
    span: Option<Span>,
    comparison: comparison::Comparison,
    isa: Isa,
}

impl AttributeComparisonStatement {
    pub fn new(span: Option<Span>, comparison: comparison::Comparison, isa: Isa) -> Self {
        Self { span, comparison, isa }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ThingConstraint {
    Isa(Isa),
    Iid(Iid),
    Has(Has),
    Links(Links),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Iid {
    span: Option<Span>,
    iid: String,
}

impl Iid {
    pub(crate) fn new(span: Option<Span>, iid: String) -> Self {
        Self { span, iid }
    }
}

impl Variable {
    pub fn iid(self, iid: impl Into<String>) -> ThingStatement {
        ThingStatement::new(None, self, vec![ThingConstraint::Iid(Iid::new(None, iid.into()))])
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum HasValue {
    Variable(Variable),
    Expression(Expression),
    Comparison(comparison::Comparison),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Has {
    span: Option<Span>,
    type_: Option<TypeAny>,
    value: HasValue,
}

impl Has {
    pub fn new(span: Option<Span>, type_: Option<TypeAny>, value: HasValue) -> Self {
        Self { span, type_, value }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Links {
    span: Option<Span>,
    relation: Relation,
}

impl Links {
    pub fn new(span: Option<Span>, relation: Relation) -> Self {
        Self { span, relation }
    }
}
