/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{token::ArithmeticOperator, Span, Spanned},
    pattern::statement::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Identifier(pub String); // TODO

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FunctionCall {
    span: Option<Span>,
    sigil: Identifier,
    args: Vec<Expression>,
}

impl FunctionCall {
    pub fn new(span: Option<Span>, sigil: Identifier, args: Vec<Expression>) -> Self {
        Self { span, sigil, args }
    }
}

impl Spanned for FunctionCall {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Operation {
    op: ArithmeticOperator,
    left: Expression,
    right: Expression,
}

impl Operation {
    pub fn new(op: ArithmeticOperator, left: Expression, right: Expression) -> Self {
        Self { op, left, right }
    }
}

impl Spanned for Operation {
    fn span(&self) -> Option<Span> {
        if let (Some(left), Some(right)) = (self.left.span(), self.right.span()) {
            Some(Span { begin: left.begin, end: right.end })
        } else {
            None
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Paren {
    span: Option<Span>,
    inner: Expression,
}

impl Paren {
    pub(crate) fn new(span: Option<Span>, inner: Expression) -> Self {
        Self { span, inner }
    }
}

impl Spanned for Paren {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Value {
    span: Option<Span>,
    inner: String, // TODO
}

impl Value {
    pub(crate) fn new(span: Option<Span>, inner: String) -> Self {
        Self { span, inner }
    }
}

impl Spanned for Value {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ListIndex {
    span: Option<Span>,
    variable: Variable,
    index: Expression,
}

impl ListIndex {
    pub fn new(span: Option<Span>, variable: Variable, index: Expression) -> Self {
        Self { span, variable, index }
    }
}

impl Spanned for ListIndex {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct List {
    span: Option<Span>,
    items: Vec<Expression>,
}

impl List {
    pub fn new(span: Option<Span>, items: Vec<Expression>) -> Self {
        Self { span, items }
    }
}

impl Spanned for List {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ListIndexRange {
    span: Option<Span>,
    var: Variable,
    from: Expression,
    to: Expression,
}

impl ListIndexRange {
    pub fn new(span: Option<Span>, var: Variable, from: Expression, to: Expression) -> Self {
        Self { span, var, from, to }
    }
}

impl Spanned for ListIndexRange {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Expression {
    Variable(Variable),
    ListIndex(Box<ListIndex>),
    Value(Value),
    Function(FunctionCall),
    Operation(Box<Operation>),
    Paren(Box<Paren>),
    List(List),
    ListIndexRange(Box<ListIndexRange>),
}

impl Spanned for Expression {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Variable(inner) => inner.span(),
            Self::ListIndex(inner) => inner.span(),
            Self::Value(inner) => inner.span(),
            Self::Function(inner) => inner.span(),
            Self::Operation(inner) => inner.span(),
            Self::Paren(inner) => inner.span(),
            Self::List(inner) => inner.span(),
            Self::ListIndexRange(inner) => inner.span(),
        }
    }
}
