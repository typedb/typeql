/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{token::Order, Span},
    pattern::statement::Variable,
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

#[derive(Debug, Eq, PartialEq)]
pub enum Modifier {
    Filter(Filter),
    Sort(Sort),
    Offset(Offset),
    Limit(Limit),
}
