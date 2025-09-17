/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{
    cmp::min,
    fmt::{Display, Formatter},
};

pub use error::Error;

use crate::common::error::{SYNTAX_ANNOTATED_INDENT, SYNTAX_ANNOTATED_INDICATOR_COL, SYNTAX_ANNOTATED_INDICATOR_LINE};

pub mod date_time;
pub mod error;
pub mod identifier;
pub mod string;
pub mod token;

pub type Result<T = ()> = std::result::Result<T, Error>;

#[cfg(feature = "quine")]
use {polyquine::Quine, proc_macro2::TokenStream};

#[derive(Debug, Copy, Clone, Eq, PartialEq, Hash)]
pub struct LineColumn {
    pub line: u32,
    pub column: u32,
}

#[derive(Debug, Copy, Clone, Eq, PartialEq, Hash)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Span {
    pub begin_offset: usize,
    pub end_offset: usize,
}

pub trait Spanned {
    fn span(&self) -> Option<Span>;
}

pub trait Spannable {
    fn extract(&self, span: Span) -> &str;

    fn line_col(&self, span: Span) -> Option<(LineColumn, LineColumn)>;

    fn extract_annotated_line_col(
        &self,
        line: usize,
        col: usize,
        lines_before: usize,
        lines_after: usize,
    ) -> Option<String>;
}

impl Spannable for &str {
    fn extract(&self, span: Span) -> &str {
        &self[span.begin_offset..span.end_offset]
    }

    fn line_col(&self, span: Span) -> Option<(LineColumn, LineColumn)> {
        let (begin_line, begin_col) = pest::Position::new(self, span.begin_offset)?.line_col();
        let (end_line, end_col) = pest::Position::new(self, span.end_offset)?.line_col();
        Some((
            LineColumn { line: begin_line as u32, column: begin_col as u32 },
            LineColumn { line: end_line as u32, column: end_col as u32 },
        ))
    }

    fn extract_annotated_line_col(
        &self,
        line: usize,
        col: usize,
        lines_before: usize,
        lines_after: usize,
    ) -> Option<String> {
        let mut annotated = false;
        let lines: Vec<_> = self
            .lines()
            .enumerate()
            .map(|(i, line_string)| {
                if i == line {
                    annotated = true;
                    format!(
                        "{SYNTAX_ANNOTATED_INDICATOR_LINE}{line_string}\n{}{SYNTAX_ANNOTATED_INDICATOR_COL}",
                        " ".repeat(SYNTAX_ANNOTATED_INDENT + col)
                    )
                } else {
                    format!("{}{line_string}", " ".repeat(SYNTAX_ANNOTATED_INDENT))
                }
            })
            .collect();
        if annotated {
            let lines_start = line.checked_sub(lines_before).unwrap_or(0);
            let lines_end = min(
                lines.len(),
                line.checked_add(lines_after.checked_add(1).unwrap_or(usize::MAX)).unwrap_or(usize::MAX),
            );
            Some(lines[lines_start..lines_end].join("\n"))
        } else {
            None
        }
    }
}

impl Display for Span {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "begin-offset: {}, end-offset: {}", self.begin_offset, self.end_offset)
    }
}
