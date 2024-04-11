/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct LabelConstraint {
    pub label: Label,
}

impl Validatable for LabelConstraint {
    fn validate(&self) -> Result {
        Ok(())
    }
}

impl fmt::Display for LabelConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Type, self.label)
    }
}
