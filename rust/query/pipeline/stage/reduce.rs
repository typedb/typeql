/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    pretty::{indent, Pretty},
    util::write_joined,
    variable::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Reduce {
    reduction: Reduction,
}

impl Reduce {
    pub fn new(reduction: Reduction) -> Self {
        Reduce { reduction }
    }
}

impl Pretty for Reduce {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{} {};", token::Clause::Reduce, self.reduction)
    }
}

impl fmt::Display for Reduce {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {};", token::Clause::Reduce, self.reduction)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Reduction {
    Check(Check),
    First(First),
    Value(Vec<ReduceValue>),
}

impl Pretty for Reduction {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(self, f)
    }
}

impl fmt::Display for Reduction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Check(inner) => fmt::Display::fmt(inner, f),
            Self::First(inner) => fmt::Display::fmt(inner, f),
            Self::Value(inner) => {
                write_joined!(f, ", ", inner)?;
                Ok(())
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Check {
    span: Option<Span>,
}

impl Check {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Pretty for Check {}

impl fmt::Display for Check {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{};", token::ReduceOperator::Check)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct First {
    span: Option<Span>,
    pub variables: Vec<Variable>,
}

impl First {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

impl Pretty for First {}

impl fmt::Display for First {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", token::ReduceOperator::First)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_str(");")?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ReduceValue {
    Count(Count),
    Stat(Stat),
}

impl Pretty for ReduceValue {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Count(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Stat(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for ReduceValue {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Count(inner) => fmt::Display::fmt(inner, f),
            Self::Stat(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Count {
    span: Option<Span>,
    pub variables: Vec<Variable>,
}

impl Count {
    pub fn new(span: Option<Span>, variables: Vec<Variable>) -> Self {
        Self { span, variables }
    }
}

impl Pretty for Count {}

impl fmt::Display for Count {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", token::ReduceOperator::Count)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_str(")")?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Stat {
    span: Option<Span>,
    pub reduce_operator: token::ReduceOperator,
    pub variable: Variable,
}

impl Stat {
    pub fn new(span: Option<Span>, aggregate: token::ReduceOperator, variable: Variable) -> Self {
        Self { span, reduce_operator: aggregate, variable }
    }
}

impl Pretty for Stat {}

impl fmt::Display for Stat {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}({})", self.reduce_operator, self.variable)
    }
}
