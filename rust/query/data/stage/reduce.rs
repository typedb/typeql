/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{token::Aggregate, Span},
    identifier::Variable,
};

#[derive(Debug, Eq, PartialEq)]
pub struct Check {
    span: Option<Span>,
}

impl Check {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct First {
    span: Option<Span>,
    variables: Vec<Variable>,
}

impl First {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Count {
    span: Option<Span>,
    variables: Vec<Variable>,
}

impl Count {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Stat {
    span: Option<Span>,
    aggregate: Aggregate,
    variable: Variable,
}

impl Stat {
    pub fn new(span: Option<Span>, aggregate: Aggregate, variable: Variable) -> Self {
        Self { span, aggregate, variable }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum ReduceAll {
    Count(Count),
    Stat(Stat),
}

#[derive(Debug, Eq, PartialEq)]
pub enum Reduce {
    Check(Check),
    First(First),
    All(Vec<ReduceAll>),
}
