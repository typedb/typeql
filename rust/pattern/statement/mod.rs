/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::common::Span;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Single(Single),
}

// FIXME move
#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Anonymous(Option<Span>),
    Named(Option<Span>, String),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Is {
    span: Option<Span>,
    lhs: Variable,
    rhs: Variable,
}

impl Is {
    pub(crate) fn new(span: Option<Span>, lhs: Variable, rhs: Variable) -> Self {
        Self { span, lhs, rhs }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Single {
    Is(Is),
}
