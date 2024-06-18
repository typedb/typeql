/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{token::Comparator, Span, Spanned},
    expression::{Expression, FunctionCall},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Single(Single),
}

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
pub struct Comparison {
    span: Option<Span>,
    lhs: Expression,
    comparator: Comparator,
    rhs: Expression,
}

impl Comparison {
    pub fn new(span: Option<Span>, lhs: Expression, (comparator, rhs): (Comparator, Expression)) -> Self {
        Self { span, lhs, comparator, rhs }
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
pub enum Single {
    Is(Is),
    InStream(InStream),
    Comparison(Comparison),
    Assignment(Assignment),
}
