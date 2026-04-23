/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, sync::OnceLock};

use regex::Regex;

use crate::{
    common::{Span, Spanned, error::TypeQLError},
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

const UNDERSCORE: &str = "_";
const HYPHEN: &str = r"\-";
const ASCII_DIGIT: &str = "0-9";
const XID_START: &str = r"\p{XID_Start}";
const XID_CONTINUE: &str = r"\p{XID_Continue}";

pub fn is_valid_label(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        let head_classes = format!("{UNDERSCORE}{XID_START}");
        let tail_classes = format!("{HYPHEN}{XID_CONTINUE}");
        Regex::new(&format!("^[{head_classes}][{tail_classes}]*$")).unwrap()
    });
    regex.is_match(identifier)
}

pub fn is_valid_var_identifier(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        let head_classes = format!("{XID_START}{ASCII_DIGIT}");
        let tail_classes = format!("{HYPHEN}{XID_CONTINUE}");
        Regex::new(&format!("^[{head_classes}][{tail_classes}]*$")).unwrap()
    });
    regex.is_match(identifier)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_is_valid_label() {
        assert!(is_valid_label("person"));
        assert!(is_valid_label("_private"));
        assert!(is_valid_label("_leading-underscore"));
        assert!(is_valid_label("type-with-hyphens"));
        assert!(is_valid_label("name123"));
        assert!(is_valid_label("café"));
        assert!(!is_valid_label("0starts-with-digit"));
        assert!(!is_valid_label("-starts-with-hyphen"));
        assert!(!is_valid_label(""));
        assert!(!is_valid_label("has space"));
    }

    #[test]
    fn test_is_valid_var_identifier() {
        assert!(is_valid_var_identifier("person"));
        assert!(is_valid_var_identifier("0starts-with-digit"));
        assert!(is_valid_var_identifier("name123"));
        assert!(is_valid_var_identifier("café"));
        assert!(!is_valid_var_identifier("_leading-underscore"));
        assert!(!is_valid_var_identifier("-starts-with-hyphen"));
        assert!(!is_valid_var_identifier(""));
        assert!(!is_valid_var_identifier("has space"));
    }
}
