/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use self::stage::Stage;
use crate::{common::{Span, Spanned}, pretty::{indent, Pretty}, schema::definable, token};

pub mod stage;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Preamble {
    span: Option<Span>,
    pub function: definable::Function,
}

impl Preamble {
    pub(crate) fn new(span: Option<Span>, function: definable::Function) -> Self {
        Self { span, function }
    }
}

impl Pretty for Preamble {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        writeln!(f, "{}", token::Clause::With)?;
        Pretty::fmt(&self.function, indent_level + 1, f)?;
        Ok(())
    }
}

impl fmt::Display for Preamble {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Clause::With, &self.function)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Pipeline {
    span: Option<Span>,
    pub preambles: Vec<Preamble>,
    pub stages: Vec<Stage>,
}

impl Pipeline {
    pub fn new(span: Option<Span>, preambles: Vec<Preamble>, stages: Vec<Stage>) -> Self {
        Self { span, preambles, stages }
    }

    pub fn build() -> Self {
        Self::new(None, Vec::new(), Vec::new())
    }

    pub fn then(mut self, stage: Stage) -> Self {
        self.stages.push(stage);
        self
    }
}

impl Spanned for Pipeline {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Pipeline {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        for preamble in &self.preambles {
            indent(indent_level, f)?;
            Pretty::fmt(preamble, indent_level, f)?;
            writeln!(f)?;
        }
        if let Some((last, rest)) = self.stages.split_last() {
            for stage in rest {
                indent(indent_level, f)?;
                Pretty::fmt(stage, indent_level, f)?;
                writeln!(f)?;
            }
            indent(indent_level, f)?;
            Pretty::fmt(last, indent_level, f)?;
        }
        Ok(())
    }
}

impl fmt::Display for Pipeline {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            for preamble in &self.preambles {
                write!(f, "{} ", preamble)?;
            }
            if let Some((last, rest)) = self.stages.split_last() {
                for stage in rest {
                    write!(f, "{} ", stage)?;
                }
                write!(f, "{}", last)?;
            }
            Ok(())
        }
    }
}
