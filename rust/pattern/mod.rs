/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use self::statement::thing::ThingStatement;
pub use self::statement::Statement;
use crate::{
    common::{token, Span},
    pretty::{indent, Pretty},
};

pub mod statement;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Conjunction {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Conjunction {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

impl Pretty for Conjunction {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        pretty_fmt_patterns(&self.patterns, indent_level, f)
    }
}

impl fmt::Display for Conjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_char('{')?;
        for pattern in &self.patterns {
            writeln!(f, " {}; ", pattern)?;
        }
        f.write_char('}')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Negation {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Negation {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

impl Pretty for Negation {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Keyword::Not)?;
        pretty_fmt_patterns(&self.patterns, indent_level + 1, f)?;
        Ok(())
    }
}

impl fmt::Display for Negation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {{", token::Keyword::Not)?;
        for pattern in &self.patterns {
            writeln!(f, " {}; ", pattern)?;
        }
        f.write_char('}')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Try {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Try {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

impl Pretty for Try {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Keyword::Not)?;
        pretty_fmt_patterns(&self.patterns, indent_level + 1, f)?;
        Ok(())
    }
}

impl fmt::Display for Try {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {{", token::Keyword::Try)?;
        for pattern in &self.patterns {
            writeln!(f, " {}; ", pattern)?;
        }
        f.write_char('}')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Disjunction {
    span: Option<Span>,
    branches: Vec<Vec<Pattern>>,
}

impl Disjunction {
    pub(crate) fn new(span: Option<Span>, branches: Vec<Vec<Pattern>>) -> Self {
        Self { span, branches }
    }
}

impl Pretty for Disjunction {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some((first, rest)) = self.branches.split_first() {
            f.write_str("{\n")?;
            for pattern in first {
                indent(indent_level + 1, f)?;
                writeln!(f, "{};", pattern)?;
            }
            f.write_char('}')?;

            for branch in rest {
                writeln!(f, " {} {{", token::Keyword::Or)?;
                for pattern in branch {
                    indent(indent_level + 1, f)?;
                    writeln!(f, "{};", pattern)?;
                }
                f.write_char('}')?;
            }
        }
        Ok(())
    }
}

impl fmt::Display for Disjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some((first, rest)) = self.branches.split_first() {
            f.write_char('{')?;
            for pattern in first {
                write!(f, " {};", pattern)?;
            }
            f.write_str(" }")?;

            for branch in rest {
                write!(f, " {} {{", token::Keyword::Or)?;
                for pattern in branch {
                    write!(f, " {};", pattern)?;
                }
                f.write_str(" }")?;
            }
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Try(Try),
    Statement(Statement),
}

impl From<ThingStatement> for Pattern {
    fn from(val: ThingStatement) -> Self {
        Pattern::Statement(Statement::Thing(val))
    }
}

impl Pretty for Pattern {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Conjunction(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Disjunction(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Negation(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Try(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Statement(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Pattern {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Conjunction(inner) => fmt::Display::fmt(inner, f),
            Self::Disjunction(inner) => fmt::Display::fmt(inner, f),
            Self::Negation(inner) => fmt::Display::fmt(inner, f),
            Self::Try(inner) => fmt::Display::fmt(inner, f),
            Self::Statement(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

fn pretty_fmt_patterns(patterns: &[Pattern], indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
    match patterns {
        [Pattern::Statement(pattern)] => {
            write!(f, "{{ {}; }}", pattern)?;
        }
        patterns => {
            f.write_str("{\n")?;
            for pattern in patterns {
                indent(indent_level, f)?;
                writeln!(f, "{};", pattern)?;
            }
            f.write_char('}')?;
        }
    }
    Ok(())
}
