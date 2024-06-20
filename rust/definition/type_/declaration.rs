/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{common::Span, pattern::Label, write_joined};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AnnotationSub {
    Abstract(Option<Span>),    // FIXME
    Cascade(Option<Span>),     // FIXME
    Independent(Option<Span>), // FIXME
}

impl fmt::Display for AnnotationSub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Abstract(_) => f.write_str("@abstract"),
            Self::Cascade(_) => f.write_str("@cascade"),
            Self::Independent(_) => f.write_str("@independent"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Sub {
    pub supertype_label: Label,
    pub annotations: Vec<AnnotationSub>,
    span: Option<Span>,
}

impl Sub {
    pub fn new(supertype_label: Label, annotations: Vec<AnnotationSub>, span: Option<Span>) -> Self {
        Self { supertype_label, annotations, span }
    }
}

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("sub ")?;
        fmt::Display::fmt(&self.supertype_label, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AnnotationValueType {
    Regex(String, Option<Span>),       // FIXME
    Values(Vec<String>, Option<Span>), // FIXME
}

impl fmt::Display for AnnotationValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Regex(regex, _) => write!(f, "@regex({regex})"),
            Self::Values(values, _) => {
                f.write_str("@values(")?;
                write_joined!(f, ", ", values)?;
                f.write_char(')')?;
                Ok(())
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    pub value_type: String, // TODO enum with optional user type?
    pub annotations: Vec<AnnotationValueType>,
    pub span: Option<Span>,
}

impl ValueType {
    pub fn new(value_type: String, annotations: Vec<AnnotationValueType>, span: Option<Span>) -> Self {
        Self { value_type, annotations, span }
    }
}

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("value ")?;
        fmt::Display::fmt(&self.value_type, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AnnotationOwns {
    Cardinality(usize, Option<usize>, Option<Span>), // FIXME
    Distinct(Option<Span>),                          // FIXME
    Key(Option<Span>),                               // FIXME
    Unique(Option<Span>),                            // FIXME
}

impl fmt::Display for AnnotationOwns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Cardinality(min, max, _) => {
                f.write_str("@card(")?;
                fmt::Display::fmt(min, f)?;
                f.write_str(", ")?;
                match max {
                    Some(max) => fmt::Display::fmt(max, f)?,
                    None => f.write_char('*')?,
                }
                f.write_char(')')?;
                Ok(())
            }
            Self::Distinct(_) => f.write_str("@distinct"),
            Self::Key(_) => f.write_str("@key"),
            Self::Unique(_) => f.write_str("@unique"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Owned {
    List(Label),
    Attribute(Label, Option<Label>),
}

impl fmt::Display for Owned {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::List(label) => write!(f, "{label}[]"),
            Self::Attribute(label, None) => write!(f, "{label}"),
            Self::Attribute(label, Some(overridden)) => write!(f, "{label} as {overridden}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    pub owned: Owned,
    pub annotations: Vec<AnnotationOwns>,
    span: Option<Span>,
}

impl Owns {
    pub fn new(owned: Owned, annotations: Vec<AnnotationOwns>, span: Option<Span>) -> Self {
        Self { owned, annotations, span }
    }
}

impl fmt::Display for Owns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("owns ")?;
        fmt::Display::fmt(&self.owned, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AnnotationRelates {
    Cardinality(usize, Option<usize>, Option<Span>), // FIXME
    Distinct(Option<Span>),                          // FIXME
    Cascade(Option<Span>),                           // FIXME
}

impl fmt::Display for AnnotationRelates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Cardinality(min, max, _) => {
                f.write_str("@card(")?;
                fmt::Display::fmt(min, f)?;
                f.write_str(", ")?;
                match max {
                    Some(max) => fmt::Display::fmt(max, f)?,
                    None => f.write_char('*')?,
                }
                f.write_char(')')?;
                Ok(())
            }
            Self::Distinct(_) => f.write_str("@distinct"),
            Self::Cascade(_) => f.write_str("@cascade"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Related {
    List(Label),
    Role(Label, Option<Label>),
}

impl fmt::Display for Related {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::List(label) => write!(f, "{label}[]"),
            Self::Role(label, None) => write!(f, "{label}"),
            Self::Role(label, Some(overridden)) => write!(f, "{label} as {overridden}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relates {
    pub related: Related,
    pub annotations: Vec<AnnotationRelates>,
    span: Option<Span>,
}

impl Relates {
    pub fn new(related: Related, annotations: Vec<AnnotationRelates>, span: Option<Span>) -> Self {
        Self { related, annotations, span }
    }
}

impl fmt::Display for Relates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("relates ")?;
        fmt::Display::fmt(&self.related, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Played {
    role: Label,
    overridden: Option<Label>,
}

impl Played {
    pub fn new(role: Label, overridden: Option<Label>) -> Self {
        Self { role, overridden }
    }
}

impl fmt::Display for Played {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.role)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " as {overridden}")?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Plays {
    played: Played,
    span: Option<Span>,
}

impl Plays {
    pub fn new(played: Played, span: Option<Span>) -> Self {
        Self { played, span }
    }
}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("plays ")?;
        fmt::Display::fmt(&self.played, f)?;
        Ok(())
    }
}
