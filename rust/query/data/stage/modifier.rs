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
    identifier::Variable,
    pretty::Pretty,
    util::write_joined,
};

#[derive(Debug, Eq, PartialEq)]
pub struct OrderedVariable {
    span: Option<Span>,
    variable: Variable,
    ordering: Option<Order>,
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

#[derive(Debug, Eq, PartialEq)]
pub struct Sort {
    span: Option<Span>,
    ordered_variables: Vec<OrderedVariable>,
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

#[derive(Debug, Eq, PartialEq)]
pub struct Filter {
    span: Option<Span>,
    variables: Vec<Variable>,
}

impl Filter {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

impl Pretty for Filter {}

impl fmt::Display for Filter {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Modifier::Filter)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_char(';')?;
        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Offset {
    span: Option<Span>,
    offset: u64,
}

impl Offset {
    pub fn new(span: Option<Span>, offset: u64) -> Self {
        Self { span, offset }
    }
}

impl Pretty for Offset {}

impl fmt::Display for Offset {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Modifier::Offset, self.offset)
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Limit {
    span: Option<Span>,
    limit: u64,
}

impl Limit {
    pub fn new(span: Option<Span>, limit: u64) -> Self {
        Self { span, limit }
    }
}

impl Pretty for Limit {}

impl fmt::Display for Limit {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Modifier::Limit, self.limit)
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum Modifier {
    Filter(Filter),
    Sort(Sort),
    Offset(Offset),
    Limit(Limit),
}

impl Pretty for Modifier {
    fn fmt(&self, indent_level: usize, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Filter(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Sort(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Offset(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Limit(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Modifier {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Filter(inner) => fmt::Display::fmt(inner, f),
            Self::Sort(inner) => fmt::Display::fmt(inner, f),
            Self::Offset(inner) => fmt::Display::fmt(inner, f),
            Self::Limit(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
