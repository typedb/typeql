/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{common::Span, pattern::Statement};

#[derive(Debug, Eq, PartialEq)]
pub struct Update {
    span: Option<Span>,
    statements: Vec<Statement>,
}

impl Update {
    pub(crate) fn new(span: Option<Span>, statements: Vec<Statement>) -> Self {
        Self { span, statements }
    }
}
