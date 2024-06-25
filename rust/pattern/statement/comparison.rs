/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::Statement;
use crate::{
    common::{token::Comparator, Span},
    expression::{Expression, Value},
    identifier::Variable,
    pattern::Pattern,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ComparisonStatement {
    span: Option<Span>,
    lhs: Expression,
    comparison: Comparison,
}

impl ComparisonStatement {
    pub fn new(span: Option<Span>, lhs: Expression, comparison: Comparison) -> Self {
        Self { span, lhs, comparison }
    }
}

impl From<ComparisonStatement> for Statement {
    fn from(val: ComparisonStatement) -> Self {
        Statement::Comparison(val)
    }
}

impl From<ComparisonStatement> for Pattern {
    fn from(val: ComparisonStatement) -> Self {
        Pattern::Statement(val.into())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Comparison {
    span: Option<Span>,
    comparator: Comparator,
    rhs: Expression,
}

impl Comparison {
    pub(crate) fn new(span: Option<Span>, comparator: Comparator, rhs: Expression) -> Self {
        Self { span, comparator, rhs }
    }
}

impl Variable {
    pub fn like(self, regex: impl Into<String>) -> ComparisonStatement {
        Expression::Variable(self).like(regex)
    }
}

impl Expression {
    pub fn like(self, regex: impl Into<String>) -> ComparisonStatement {
        ComparisonStatement::new(
            None,
            self,
            Comparison::new(None, Comparator::Like, Expression::Value(Value::new(None, regex.into()))),
        )
    }
}
