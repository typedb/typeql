/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{common::error::TypeQLError, pattern::ThingStatement, Result};

pub trait Writable {
    fn statements(self) -> Vec<ThingStatement>;
}

impl Writable for ThingStatement {
    fn statements(self) -> Vec<ThingStatement> {
        vec![self]
    }
}

impl<const N: usize> Writable for [ThingStatement; N] {
    fn statements(self) -> Vec<ThingStatement> {
        self.to_vec()
    }
}

impl Writable for Vec<ThingStatement> {
    fn statements(self) -> Vec<ThingStatement> {
        self
    }
}

pub(crate) fn validate_non_empty(statements: &[ThingStatement]) -> Result {
    if statements.is_empty() {
        Err(TypeQLError::MissingPatterns)?
    }
    Ok(())
}
