/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    pattern::{LeftOperand, ValueStatement},
    variable::variable::validate_variable_name,
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum ValueVariable {
    Named(String),
}

impl ValueVariable {
    pub fn into_value(self) -> ValueStatement {
        ValueStatement::new(self)
    }

    pub fn is_named(&self) -> bool {
        true
    }

    pub fn is_visible(&self) -> bool {
        true
    }

    pub fn name(&self) -> &str {
        let Self::Named(name) = self;
        name
    }
}

impl Validatable for ValueVariable {
    fn validate(&self) -> Result {
        match self {
            Self::Named(n) => validate_variable_name(n),
        }
    }
}

impl From<&str> for ValueVariable {
    fn from(name: &str) -> Self {
        ValueVariable::Named(name.to_owned())
    }
}

impl From<String> for ValueVariable {
    fn from(name: String) -> Self {
        ValueVariable::Named(name)
    }
}

impl LeftOperand for ValueVariable {}

impl fmt::Display for ValueVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", token::Char::Question, self.name())
    }
}
