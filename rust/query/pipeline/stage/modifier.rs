/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{
        token::{self, Order},
        Span, Spanned,
    },
    pretty::Pretty,
    query::stage::Reduce,
    util::write_joined,
    value::IntegerLiteral,
    variable::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct OrderedVariable {
    pub span: Option<Span>,
    pub variable: Variable,
    pub ordering: Option<Order>,
}

impl OrderedVariable {
    pub fn new(span: Option<Span>, variable: Variable, ordering: Option<Order>) -> Self {
        Self { span, variable, ordering }
    }
}

impl Spanned for OrderedVariable {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for OrderedVariable {}

impl fmt::Display for OrderedVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(ordering) = self.ordering {
            write!(f, " {}", ordering)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Sort {
    pub span: Option<Span>,
    pub ordered_variables: Vec<OrderedVariable>,
}

impl Sort {
    pub fn new(span: Option<Span>, ordered_variables: Vec<OrderedVariable>) -> Self {
        Self { span, ordered_variables }
    }
}

impl Spanned for Sort {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Sort {}

impl fmt::Display for Sort {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Operator::Sort)?;
        write_joined!(f, ", ", self.ordered_variables)?;
        f.write_char(';')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Select {
    pub span: Option<Span>,
    pub variables: Vec<Variable>,
}

impl Select {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

impl Spanned for Select {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Select {}

impl fmt::Display for Select {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Operator::Select)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_char(';')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Offset {
    pub span: Option<Span>,
    pub offset: IntegerLiteral,
}

impl Offset {
    pub fn new(span: Option<Span>, offset: IntegerLiteral) -> Self {
        Self { span, offset }
    }
}

impl Spanned for Offset {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Offset {}

impl fmt::Display for Offset {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Operator::Offset, self.offset)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Limit {
    pub span: Option<Span>,
    pub limit: IntegerLiteral,
}

impl Limit {
    pub fn new(span: Option<Span>, limit: IntegerLiteral) -> Self {
        Self { span, limit }
    }
}

impl Spanned for Limit {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Limit {}

impl fmt::Display for Limit {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Operator::Limit, self.limit)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Require {
    pub span: Option<Span>,
    pub variables: Vec<Variable>,
}

impl Require {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

impl Spanned for Require {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Require {}

impl fmt::Display for Require {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Operator::Require)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_char(';')?;
        Ok(())
    }
}


#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Distinct {
    span: Option<Span>,
}

impl Distinct {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Distinct {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Distinct {}

impl fmt::Display for Distinct {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Operator::Distinct)?;
        f.write_char(';')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Operator {
    Select(Select),
    Sort(Sort),
    Offset(Offset),
    Limit(Limit),
    Reduce(Reduce),
    Require(Require),
    Distinct(Distinct),
}

impl Spanned for Operator {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Select(inner) => inner.span(),
            Self::Sort(inner) => inner.span(),
            Self::Offset(inner) => inner.span(),
            Self::Limit(inner) => inner.span(),
            Self::Reduce(inner) => inner.span(),
            Self::Require(inner) => inner.span(),
            Self::Distinct(inner) => inner.span(),
        }
    }
}

impl Pretty for Operator {
    fn fmt(&self, indent_level: usize, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Select(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Sort(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Offset(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Limit(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Reduce(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Require(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Distinct(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Operator {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Select(inner) => fmt::Display::fmt(inner, f),
            Self::Sort(inner) => fmt::Display::fmt(inner, f),
            Self::Offset(inner) => fmt::Display::fmt(inner, f),
            Self::Limit(inner) => fmt::Display::fmt(inner, f),
            Self::Reduce(inner) => fmt::Display::fmt(inner, f),
            Self::Require(inner) => fmt::Display::fmt(inner, f),
            Self::Distinct(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
