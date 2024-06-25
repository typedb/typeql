/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{common::Span, write_joined};

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
