/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::common::{
    error::TypeQLError, identifier::is_valid_label_identifier, token, validatable::Validatable, Result,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Label {
    pub scope: Option<String>,
    pub name: String,
}

impl From<token::Type> for Label {
    fn from(name: token::Type) -> Self {
        Label::from(name.to_string())
    }
}

impl From<&str> for Label {
    fn from(name: &str) -> Self {
        Label::from(String::from(name))
    }
}

impl From<String> for Label {
    fn from(name: String) -> Self {
        Label { scope: None, name }
    }
}

impl From<(&str, &str)> for Label {
    fn from((scope, name): (&str, &str)) -> Self {
        Label::from((scope.to_string(), name.to_string()))
    }
}

impl From<(String, String)> for Label {
    fn from((scope, name): (String, String)) -> Self {
        Label { scope: Some(scope), name }
    }
}

impl Validatable for Label {
    fn validate(&self) -> Result {
        validate_label(&self.name)?;
        if let Some(scope_name) = &self.scope {
            validate_label(scope_name)?
        }
        Ok(())
    }
}

fn validate_label(label: &str) -> Result {
    if !is_valid_label_identifier(label) {
        Err(TypeQLError::InvalidTypeLabel { label: label.to_owned() })?
    } else {
        Ok(())
    }
}

impl fmt::Display for Label {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(scope) = &self.scope {
            write!(f, "{scope}:")?;
        }
        write!(f, "{}", self.name)
    }
}
