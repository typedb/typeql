/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::common::{error::TypeQLError, token, validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IIDConstraint {
    pub iid: String,
}

fn is_valid_iid(iid: &str) -> bool {
    iid.starts_with("0x") && iid.chars().skip(2).all(|c| c.is_ascii_hexdigit() && !c.is_uppercase())
}

impl IIDConstraint {
    pub fn new(iid: String) -> Self {
        IIDConstraint { iid }
    }
}

impl Validatable for IIDConstraint {
    fn validate(&self) -> Result {
        if !is_valid_iid(&self.iid) {
            Err(TypeQLError::InvalidIIDString { iid: self.iid.clone() })?
        }
        Ok(())
    }
}

impl From<&str> for IIDConstraint {
    fn from(iid: &str) -> Self {
        IIDConstraint::new(iid.to_string())
    }
}

impl From<String> for IIDConstraint {
    fn from(iid: String) -> Self {
        IIDConstraint::new(iid)
    }
}

impl fmt::Display for IIDConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::IID, self.iid)
    }
}
