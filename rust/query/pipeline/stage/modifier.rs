/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{
        token::{self, Order},
        Span,
    },
    pretty::Pretty,
    util::write_joined,
    value::IntegerLiteral,
    variable::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct OrderedVariable {
    span: Option<Span>,
    pub variable: Variable,
    pub ordering: Option<Order>,
}

impl OrderedVariable {
    pub fn new(span: Option<Span>, variable: Variable, ordering: Option<Order>) -> Self {
        Self { span, variable, ordering }
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
    span: Option<Span>,
    pub ordered_variables: Vec<OrderedVariable>,
}

impl Sort {
    pub fn new(span: Option<Span>, ordered_variables: Vec<OrderedVariable>) -> Self {
        Self { span, ordered_variables }
    }
}

impl Pretty for Sort {}

impl fmt::Display for Sort {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Modifier::Sort)?;
        write_joined!(f, ", ", self.ordered_variables)?;
        f.write_char(';')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Select {
    span: Option<Span>,
    pub variables: Vec<Variable>,
}

impl Select {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

impl Pretty for Select {}

impl fmt::Display for Select {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Modifier::Select)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_char(';')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Offset {
    span: Option<Span>,
    pub offset: IntegerLiteral,
}

impl Offset {
    pub fn new(span: Option<Span>, offset: IntegerLiteral) -> Self {
        Self { span, offset }
    }
}

impl Pretty for Offset {}

impl fmt::Display for Offset {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Modifier::Offset, self.offset)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Limit {
    span: Option<Span>,
    pub limit: IntegerLiteral,
}

impl Limit {
    pub fn new(span: Option<Span>, limit: IntegerLiteral) -> Self {
        Self { span, limit }
    }
}

impl Pretty for Limit {}

impl fmt::Display for Limit {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Modifier::Limit, self.limit)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Modifier {
    Select(Select),
    Sort(Sort),
    Offset(Offset),
    Limit(Limit),
}

impl Pretty for Modifier {
    fn fmt(&self, indent_level: usize, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Select(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Sort(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Offset(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Limit(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Modifier {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Select(inner) => fmt::Display::fmt(inner, f),
            Self::Sort(inner) => fmt::Display::fmt(inner, f),
            Self::Offset(inner) => fmt::Display::fmt(inner, f),
            Self::Limit(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
