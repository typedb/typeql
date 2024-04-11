/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use regex::Regex;

use crate::common::{error::TypeQLError, string::escape_regex, token, validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RegexConstraint {
    regex: String,
}

impl Validatable for RegexConstraint {
    fn validate(&self) -> Result {
        if Regex::new(&self.regex).is_err() {
            Err(TypeQLError::InvalidAttributeTypeRegex { regex: self.regex.clone() })?;
        }
        Ok(())
    }
}

impl From<&str> for RegexConstraint {
    fn from(regex: &str) -> Self {
        RegexConstraint { regex: regex.to_string() }
    }
}

impl From<String> for RegexConstraint {
    fn from(regex: String) -> Self {
        RegexConstraint { regex }
    }
}

impl fmt::Display for RegexConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, r#"{} "{}""#, token::Constraint::Regex, escape_regex(&self.regex))
    }
}
