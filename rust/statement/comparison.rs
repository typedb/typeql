/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span, Spanned},
    expression::Expression,
    pretty::Pretty,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ComparisonStatement {
    pub span: Option<Span>,
    pub lhs: Expression,
    pub comparison: Comparison,
}

impl ComparisonStatement {
    pub fn new(span: Option<Span>, lhs: Expression, comparison: Comparison) -> Self {
        Self { span, lhs, comparison }
    }
}

impl Spanned for ComparisonStatement {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for ComparisonStatement {}

impl fmt::Display for ComparisonStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.lhs, self.comparison)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Comparison {
    pub span: Option<Span>,
    pub comparator: token::Comparator,
    pub rhs: Expression,
}

impl Comparison {
    pub(crate) fn new(span: Option<Span>, comparator: token::Comparator, rhs: Expression) -> Self {
        Self { span, comparator, rhs }
    }
}

impl Spanned for Comparison {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Comparison {}

impl fmt::Display for Comparison {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.comparator, self.rhs)
    }
}
