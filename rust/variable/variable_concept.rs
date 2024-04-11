/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    pattern::LeftOperand,
    variable::variable::validate_variable_name,
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum ConceptVariable {
    Anonymous,
    Hidden,
    Named(String),
}

impl ConceptVariable {
    const ANONYMOUS_NAME: &'static str = token::Char::Underscore.as_str();

    pub fn is_visible(&self) -> bool {
        self != &Self::Hidden
    }

    pub fn is_named(&self) -> bool {
        matches!(self, Self::Named(_))
    }

    pub fn name(&self) -> &str {
        match self {
            Self::Anonymous | Self::Hidden => Self::ANONYMOUS_NAME,
            Self::Named(name) => name,
        }
    }
}

impl Validatable for ConceptVariable {
    fn validate(&self) -> Result {
        match self {
            Self::Anonymous | Self::Hidden => Ok(()),
            Self::Named(n) => validate_variable_name(n),
        }
    }
}

impl From<()> for ConceptVariable {
    fn from(_: ()) -> Self {
        ConceptVariable::Anonymous
    }
}

impl From<&str> for ConceptVariable {
    fn from(name: &str) -> Self {
        ConceptVariable::Named(name.to_string())
    }
}

impl From<String> for ConceptVariable {
    fn from(name: String) -> Self {
        ConceptVariable::Named(name)
    }
}

impl LeftOperand for ConceptVariable {}

impl fmt::Display for ConceptVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", token::Char::Dollar, self.name())
    }
}
