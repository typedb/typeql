/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::Label;
use crate::{
    common::{token::Comparator, Span, Spanned},
    expression::{Expression, FunctionCall, Value},
};

// FIXME move
#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Anonymous(Option<Span>),
    Named(Option<Span>, String),
}

impl Spanned for Variable {
    fn span(&self) -> Option<Span> {
        match self {
            Variable::Anonymous(span) | Variable::Named(span, _) => *span,
        }
    }
}

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
pub struct ComparisonStatement {
    span: Option<Span>,
    lhs: Expression,
    comparison: Comparison,
}

impl ComparisonStatement {
    pub fn new(span: Option<Span>, lhs: Expression, comparison: Comparison) -> Self {
        Self { span, lhs, comparison }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Comparison {
    span: Option<Span>,
    comparator: Comparator,
    rhs: Expression,
}

impl Comparison {
    pub fn new(span: Option<Span>, comparator: Comparator, rhs: Expression) -> Self {
        Self { span, comparator, rhs }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Assignment {
    span: Option<Span>,
    lhs: Vec<Variable>, // TODO or destructured struct
    rhs: Expression,
}

impl Assignment {
    pub fn new(span: Option<Span>, lhs: Vec<Variable>, rhs: Expression) -> Self {
        Self { span, lhs, rhs }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Type {
    Label(Label),
    Variable(Variable),
    ListLabel(Label),
    ListVariable(Variable),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum RolePlayer {
    Typed(Type, Variable),
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

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ThingConstraint {
    Isa(Isa),
    Iid(Iid),
    Has(Has),
    Links(Links),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum IsaKind {
    Exact,
    Subtype,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Isa {
    span: Option<Span>,
    kind: IsaKind,
    type_: Type,
}

impl Isa {
    pub fn new(span: Option<Span>, kind: IsaKind, type_: Type) -> Self {
        Self { span, kind, type_ }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Iid {
    span: Option<Span>,
    iid: String,
}

impl Iid {
    pub fn new(span: Option<Span>, iid: String) -> Self {
        Self { span, iid }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum HasValue {
    Variable(Variable),
    Expression(Expression),
    Comparison(Comparison),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Has {
    span: Option<Span>,
    type_: Option<Type>,
    value: HasValue,
}

impl Has {
    pub fn new(span: Option<Span>, type_: Option<Type>, value: HasValue) -> Self {
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
    comparison: Comparison,
    isa: Isa,
}

impl AttributeComparisonStatement {
    pub fn new(span: Option<Span>, comparison: Comparison, isa: Isa) -> Self {
        Self { span, comparison, isa }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Is(Is),
    InStream(InStream),
    Comparison(ComparisonStatement),
    Assignment(Assignment),
    Thing(ThingStatement),
    Relation(RelationStatement),
    AttributeValue(AttributeValueStatement),
    AttributeComparison(AttributeComparisonStatement),
}
