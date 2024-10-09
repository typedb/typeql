/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{Display, Formatter};

pub use error::Error;

pub mod date_time;
pub mod error;
pub(crate) mod identifier;
pub mod string;
pub mod token;

pub type Result<T = ()> = std::result::Result<T, Error>;

#[derive(Debug, Copy, Clone, Eq, PartialEq, Hash)]
pub struct LineColumn {
    pub line: u32,
    pub column: u32,
}

impl Display for LineColumn {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}:{}", self.line, self.column)
    }
}

#[derive(Debug, Copy, Clone, Eq, PartialEq, Hash)]
pub struct Span {
    pub begin: LineColumn,
    pub end: LineColumn,
}

pub trait Spanned {
    fn span(&self) -> Option<Span>;
}

pub trait DisplaySpanned: Spanned {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result;
}

impl<T: Spanned + Display> DisplaySpanned for T {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        if self.span().is_some() {
            // TODO: experiment to see if including span end is helpful
            write!(f, "Declaration at {}:\n{}", self.span().unwrap().begin, self)
        } else {
            write!(f, "{}", self)
        }
    }
}

impl Display for Span {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        // TODO: test if writing the end as well looks better!
        write!(f, "{}", self.begin)
    }
}
