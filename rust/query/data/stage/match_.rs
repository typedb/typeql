/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use super::Stage;
use crate::{common::Span, pattern::Pattern, query::Query};

#[derive(Debug, Eq, PartialEq)]
pub struct Match {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Match {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }

    pub fn build(patterns: Vec<Pattern>) -> Self {
        Self::new(None, patterns)
    }

    pub fn and(mut self, pattern: Pattern) -> Self {
        self.patterns.push(pattern);
        self
    }
}

impl From<Match> for Query {
    fn from(value: Match) -> Self {
        Self::Data(crate::query::DataQuery::new(None, Vec::new(), vec![Stage::Match(value)]))
    }
}

impl fmt::Display for Match {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}
