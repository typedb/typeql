/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span, Spanned},
    pattern::Pattern,
    pretty::{indent, Pretty},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Put {
    pub span: Option<Span>,
    pub patterns: Vec<Pattern>,
}

impl Put {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

impl Spanned for Put {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Put {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Put)?;
        for pattern in &self.patterns {
            writeln!(f)?;
            indent(indent_level, f)?;
            Pretty::fmt(pattern, indent_level, f)?;
            f.write_char(';')?;
        }
        Ok(())
    }
}

impl fmt::Display for Put {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            write!(f, "{}", token::Clause::Put)?;
            for pattern in &self.patterns {
                write!(f, " {pattern};")?;
            }
            Ok(())
        }
    }
}
