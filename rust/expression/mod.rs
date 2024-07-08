/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{
        identifier::Identifier,
        token::{self, ArithmeticOperator},
        Span, Spanned,
    },
    pretty::Pretty,
    util::write_joined,
    value::Literal,
    variable::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct BuiltinFunctionName {
    span: Option<Span>,
    token: token::Function,
}

impl BuiltinFunctionName {
    pub fn new(span: Option<Span>, token: token::Function) -> Self {
        Self { span, token }
    }
}

impl Pretty for BuiltinFunctionName {}

impl fmt::Display for BuiltinFunctionName {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.token)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum FunctionName {
    Builtin(BuiltinFunctionName),
    Identifier(Identifier),
}

impl Pretty for FunctionName {}

impl fmt::Display for FunctionName {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Builtin(inner) => fmt::Display::fmt(inner, f),
            Self::Identifier(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FunctionCall {
    span: Option<Span>,
    name: FunctionName,
    args: Vec<Expression>,
}

impl FunctionCall {
    pub fn new(span: Option<Span>, name: FunctionName, args: Vec<Expression>) -> Self {
        Self { span, name, args }
    }
}

impl Spanned for FunctionCall {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for FunctionCall {}

impl fmt::Display for FunctionCall {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", self.name)?;
        write_joined!(f, ", ", self.args)?;
        f.write_char(')')?;
        Ok(())
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

impl Pretty for Operation {}

impl fmt::Display for Operation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.left, self.op, self.right)
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

impl Pretty for Paren {}

impl fmt::Display for Paren {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "({})", self.inner)
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

impl Pretty for ListIndex {}

impl fmt::Display for ListIndex {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}[{}]", self.variable, self.index)
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

impl Pretty for List {}

impl fmt::Display for List {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_char('[')?;
        write_joined!(f, ", ", self.items)?;
        f.write_char(']')?;
        Ok(())
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

impl Pretty for ListIndexRange {}

impl fmt::Display for ListIndexRange {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}[{}..{}]", self.var, self.from, self.to)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Expression {
    Variable(Variable),
    ListIndex(Box<ListIndex>),
    Value(Literal),
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

impl Pretty for Expression {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variable(inner) => Pretty::fmt(inner, indent_level, f),
            Self::ListIndex(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Value(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Function(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Operation(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Paren(inner) => Pretty::fmt(inner, indent_level, f),
            Self::List(inner) => Pretty::fmt(inner, indent_level, f),
            Self::ListIndexRange(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Expression {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variable(inner) => fmt::Display::fmt(inner, f),
            Self::ListIndex(inner) => fmt::Display::fmt(inner, f),
            Self::Value(inner) => fmt::Display::fmt(inner, f),
            Self::Function(inner) => fmt::Display::fmt(inner, f),
            Self::Operation(inner) => fmt::Display::fmt(inner, f),
            Self::Paren(inner) => fmt::Display::fmt(inner, f),
            Self::List(inner) => fmt::Display::fmt(inner, f),
            Self::ListIndexRange(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
