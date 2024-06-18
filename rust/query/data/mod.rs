/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use self::stage::Stage;
use crate::common::{Span, Spanned};

pub mod stage;

#[derive(Debug, Eq, PartialEq)]
pub struct DataQuery {
    span: Option<Span>,
    stages: Vec<Stage>,
}

impl DataQuery {
    pub(crate) fn new(span: Option<Span>) -> Self {
        Self { span, stages: Vec::new() }
    }

    pub fn build() -> Self {
        Self::new(None)
    }

    pub fn then(mut self, stage: Stage) -> Self {
        self.stages.push(stage);
        self
    }
}

impl Spanned for DataQuery {
    fn span(&self) -> Option<Span> {
        todo!()
    }
}

impl fmt::Display for DataQuery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}
