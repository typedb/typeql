/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::common::{error::TypeQLError, identifier::is_valid_label_identifier, Result, Span, Spanned};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Label {
    pub scope: Option<String>,
    pub name: String,
    span: Option<Span>,
}

impl Label {
    pub(crate) fn unscoped(name: impl AsRef<str>, span: Option<Span>) -> Self {
        Self { scope: None, name: name.as_ref().to_owned(), span }
    }
}

impl Spanned for Label {
    fn span(&self) -> Option<Span> {
        self.span
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
