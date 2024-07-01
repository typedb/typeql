/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span},
    expression::Value,
    util::write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Abstract {
    span: Option<Span>,
}

impl Abstract {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl fmt::Display for Abstract {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Abstract)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Cardinality {
    span: Option<Span>,
    min: usize,
    max: Option<usize>,
}

impl Cardinality {
    pub fn new(span: Option<Span>, min: usize, max: Option<usize>) -> Self {
        Self { span, min, max }
    }
}

impl fmt::Display for Cardinality {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}({}..", token::Annotation::Cardinality, self.min)?;
        if let Some(max) = self.max {
            write!(f, "{}", max)?;
        }
        f.write_char(')')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Cascade {
    span: Option<Span>,
}

impl Cascade {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl fmt::Display for Cascade {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Cascade)
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

impl fmt::Display for Distinct {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Distinct)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Independent {
    span: Option<Span>,
}

impl Independent {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl fmt::Display for Independent {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Independent)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Key {
    span: Option<Span>,
}

impl Key {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl fmt::Display for Key {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Key)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Range {
    span: Option<Span>,
    min: Option<Value>,
    max: Option<Value>,
}

impl Range {
    pub fn new(span: Option<Span>, min: Option<Value>, max: Option<Value>) -> Self {
        Self { span, min, max }
    }
}

impl fmt::Display for Range {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}(", token::Annotation::Range)?;
        if let Some(min) = &self.min {
            write!(f, "{}", min)?;
        }
        f.write_str("..")?;
        if let Some(max) = &self.max {
            write!(f, "{}", max)?;
        }
        f.write_char(')')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Regex {
    span: Option<Span>,
    regex: String,
}

impl Regex {
    pub fn new(span: Option<Span>, regex: String) -> Self {
        Self { span, regex }
    }
}

impl fmt::Display for Regex {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}({})", token::Annotation::Regex, self.regex)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Unique {
    span: Option<Span>,
}

impl Unique {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl fmt::Display for Unique {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Unique)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Values {
    span: Option<Span>,
    values: Vec<String>, // FIXME
}

impl Values {
    pub fn new(span: Option<Span>, values: Vec<String>) -> Self {
        Self { span, values }
    }
}

impl fmt::Display for Values {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}(", token::Annotation::Values)?;
        write_joined!(f, ", ", &self.values)?;
        f.write_char(')')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Annotation {
    Abstract(Abstract),
    Cardinality(Cardinality),
    Cascade(Cascade),
    Distinct(Distinct),
    Independent(Independent),
    Key(Key),
    Range(Range),
    Regex(Regex),
    Unique(Unique),
    Values(Values),
}

impl fmt::Display for Annotation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Abstract(inner) => fmt::Display::fmt(inner, f),
            Self::Cardinality(inner) => fmt::Display::fmt(inner, f),
            Self::Cascade(inner) => fmt::Display::fmt(inner, f),
            Self::Distinct(inner) => fmt::Display::fmt(inner, f),
            Self::Independent(inner) => fmt::Display::fmt(inner, f),
            Self::Key(inner) => fmt::Display::fmt(inner, f),
            Self::Range(inner) => fmt::Display::fmt(inner, f),
            Self::Regex(inner) => fmt::Display::fmt(inner, f),
            Self::Unique(inner) => fmt::Display::fmt(inner, f),
            Self::Values(inner) => fmt::Display::fmt(inner, f),
        }
    }
}