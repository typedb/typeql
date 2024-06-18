/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{common::Span, pattern::Pattern};

#[derive(Debug, Eq, PartialEq)]
pub struct Match {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Match {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}
