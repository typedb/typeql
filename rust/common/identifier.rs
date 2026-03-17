/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, sync::OnceLock};

use regex::Regex;

use crate::{
    common::{error::TypeQLError, Span, Spanned},
    is_reserved_keyword,
    pretty::Pretty,
};

#[derive(Debug, Clone, Eq, PartialEq, Hash)]
pub struct Identifier {
    pub span: Option<Span>,
    ident: String,
}

impl Identifier {
    pub fn new(span: Option<Span>, ident: String) -> Self {
        Self { span, ident }
    }

    pub fn as_str_unreserved(&self) -> Result<&str, TypeQLError> {
        if !is_reserved_keyword(&self.ident) {
            Ok(&self.ident)
        } else {
            Err(TypeQLError::ReservedKeywordAsIdentifier { identifier: self.clone() })
        }
    }

    pub fn as_str_unchecked(&self) -> &str {
        &self.ident
    }
}

impl Pretty for Identifier {}

impl fmt::Display for Identifier {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.ident, f)
    }
}

impl Spanned for Identifier {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl From<&str> for Identifier {
    fn from(value: &str) -> Self {
        Self::new(None, value.to_owned())
    }
}

impl From<String> for Identifier {
    fn from(value: String) -> Self {
        Self::new(None, value)
    }
}

pub fn is_valid_label(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| Regex::new(r"^[_\p{XID_Start}][\-\p{XID_Continue}]*$").unwrap());
    regex.is_match(identifier)
}

const ASCII_DIGIT: &str = "0-9";

pub fn is_valid_var_identifier(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        Regex::new(&format!(r"^[\p{{XID_Start}}{ASCII_DIGIT}][\-\p{{XID_Continue}}]*$")).unwrap()
    });
    regex.is_match(identifier)
}
