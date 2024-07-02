/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span},
    identifier::Variable,
    pretty::Pretty,
    util::write_joined,
};

#[derive(Debug, Eq, PartialEq)]
pub enum Reduce {
    Check(Check),
    First(First),
    All(Vec<ReduceAll>),
}

impl Pretty for Reduce {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Check(inner) => Pretty::fmt(inner, indent_level, f),
            Self::First(inner) => Pretty::fmt(inner, indent_level, f),
            Self::All(inner) => {
                write_joined!(f, ", ", inner)?;
                f.write_char(';')?;
                Ok(())
            }
        }
    }
}

impl fmt::Display for Reduce {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Check(inner) => fmt::Display::fmt(inner, f),
            Self::First(inner) => fmt::Display::fmt(inner, f),
            Self::All(inner) => {
                write_joined!(f, ", ", inner)?;
                Ok(())
            }
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
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
        write!(f, "{};", token::Aggregate::Check)
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

impl Pretty for First {}

impl fmt::Display for First {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", token::Aggregate::First)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_str(");")?;
        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum ReduceAll {
    Count(Count),
    Stat(Stat),
}

impl Pretty for ReduceAll {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Count(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Stat(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for ReduceAll {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Count(inner) => fmt::Display::fmt(inner, f),
            Self::Stat(inner) => fmt::Display::fmt(inner, f),
        }
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

impl Pretty for Count {}

impl fmt::Display for Count {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", token::Aggregate::Count)?;
        write_joined!(f, ", ", self.variables)?;
        f.write_str(")")?;
        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Stat {
    span: Option<Span>,
    aggregate: token::Aggregate,
    variable: Variable,
}

impl Stat {
    pub fn new(span: Option<Span>, aggregate: token::Aggregate, variable: Variable) -> Self {
        Self { span, aggregate, variable }
    }
}

impl Pretty for Stat {}

impl fmt::Display for Stat {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}({})", self.aggregate, self.variable)
    }
}
