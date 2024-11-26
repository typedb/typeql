/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{Display, Formatter};

pub use error::Error;

pub mod date_time;
pub mod error;
pub mod identifier;
pub mod string;
pub mod token;

pub type Result<T = ()> = std::result::Result<T, Error>;

#[derive(Debug, Copy, Clone, Eq, PartialEq, Hash)]
pub struct LineColumn {
    pub line: u32,
    pub column: u32,
}

#[derive(Debug, Copy, Clone, Eq, PartialEq, Hash)]
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
}

pub trait DisplaySpanned: Spanned {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result;
}

impl<T: Spanned + Display> DisplaySpanned for T {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        if self.span().is_some() {
            // TODO: experiment to see if including span end is helpful
            write!(f, "Declaration at {}:\n{}", self.span().unwrap().begin_offset, self)
        } else {
            write!(f, "{}", self)
        }
    }
}

impl Display for Span {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        // TODO: test if writing the end as well looks better!
        write!(f, "{}", self.begin_offset)
    }
}
