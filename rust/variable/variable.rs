/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;
use std::fmt::Formatter;

use crate::{
    common::{Result, token, validatable::Validatable},
    pattern::LeftOperand,
};
use crate::common::error::TypeQLError;
use crate::common::identifier::is_valid_var_identifier;

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum Variable {
    Anonymous,
    Hidden,
    Named(String),
}

impl Variable {
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

    pub fn as_ref(&self) -> VariableRef<'_> {
        VariableRef::Concept(self)
    }
}

impl Validatable for Variable {
    fn validate(&self) -> Result {
        match self {
            Self::Anonymous | Self::Hidden => Ok(()),
            Self::Named(n) => validate_variable_name(n),
        }
    }
}

impl From<()> for Variable {
    fn from(_: ()) -> Self {
        Variable::Anonymous
    }
}

impl From<&str> for Variable {
    fn from(name: &str) -> Self {
        Variable::Named(name.to_string())
    }
}

impl From<String> for Variable {
    fn from(name: String) -> Self {
        Variable::Named(name)
    }
}

impl LeftOperand for Variable {}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", token::Char::Dollar, self.name())
    }
}


#[derive(Debug, Clone, Copy, Eq, Hash, PartialEq)]
pub enum VariableRef<'a> {
    Concept(&'a Variable),
}

impl VariableRef<'_> {
    pub fn is_name(&self) -> bool {
        match self {
            VariableRef::Concept(var) => (*var).is_named(),
        }
    }

    pub fn to_owned(self) -> Variable {
        match self {
            Self::Concept(var) => var.clone(),
        }
    }
}

impl fmt::Display for VariableRef<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            VariableRef::Concept(var) => write!(f, "{}", *var),
        }
    }
}

pub(crate) fn validate_variable_name(name: &str) -> Result {
    if !is_valid_var_identifier(name) {
        Err(TypeQLError::InvalidVariableName { name: name.to_owned() })?
    }
    Ok(())
}
