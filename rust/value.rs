/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{error::TypeQLError, Span, Spanned},
    pretty::Pretty,
    Result,
};

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum Tag {
    Boolean,
    Long,
    Double,
    Decimal,
    Date,
    DateTime,
    DateTimeTZ,
    Duration,
    String,
    // larger groupings when ambiguous
    Integral,   // Long OR Fractional
    Fractional, // Double OR Decimal
}

impl Tag {
    fn name(&self) -> &'static str {
        match self {
            Self::Boolean => "Boolean",
            Self::Long => "Long",
            Self::Double => "Double",
            Self::Decimal => "Decimal",
            Self::Date => "Date",
            Self::DateTime => "DateTime",
            Self::DateTimeTZ => "DateTimeTZ",
            Self::Duration => "Duration",
            Self::String => "String",
            Self::Integral => "Integral",
            Self::Fractional => "Fractional",
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Literal {
    span: Option<Span>,
    pub tag: Option<Tag>,
    inner: String, // TODO this can be smarter
}

impl Literal {
    pub(crate) fn new(span: Option<Span>, category: Option<Tag>, inner: String) -> Self {
        Self { span, tag: category, inner }
    }

    pub fn parse_to_string(&self) -> Result<String> {
        if self.tag.is_some_and(|cat| cat != Tag::String) {
            Err(TypeQLError::InvalidLiteral { expected_variant: "String", variant: self.tag.unwrap().name() }.into())
        } else {
            parse_string(&self.inner)
        }
    }
}

impl Spanned for Literal {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Literal {}

impl fmt::Display for Literal {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str(&self.inner)
    }
}

fn parse_string(escaped_string: &str) -> Result<String> {
    let bytes = escaped_string.as_bytes();
    // it's a bug if these fail; either in the parser or the builder
    assert_eq!(bytes[0], bytes[bytes.len() - 1]);
    assert!(matches!(bytes[0], b'\'' | b'"'));
    let escaped_string = &escaped_string[1..escaped_string.len() - 1];

    let mut buf = String::with_capacity(escaped_string.len());

    let mut rest = escaped_string;
    while !rest.is_empty() {
        let (char, escaped_len) = if rest.as_bytes()[0] == b'\\' {
            let bytes = rest.as_bytes();

            if bytes.len() < 2 {
                return Err(TypeQLError::InvalidStringEscape {
                    full_string: escaped_string.to_owned(),
                    escape: String::from(r"\"),
                }
                .into());
            }

            match bytes[1] {
                BSP => ('\x08', 2),
                TAB => ('\x09', 2),
                LF_ => ('\x0a', 2),
                FF_ => ('\x0c', 2),
                CR_ => ('\x0d', 2),
                c @ (b'"' | b'\'' | b'\\') => (c as char, 2),
                b'u' => todo!("Unicode escape handling"),
                _ => {
                    return Err(TypeQLError::InvalidStringEscape {
                        full_string: escaped_string.to_owned(),
                        escape: format!(r"\{}", rest.chars().nth(1).unwrap()),
                    }
                    .into())
                }
            }
        } else {
            let char = rest.chars().next().expect("string is non-empty");
            (char, char.len_utf8())
        };
        buf.push(char);
        rest = &rest[escaped_len..];
    }
    Ok(buf)
}

const BSP: u8 = b'b';
const TAB: u8 = b't';
const LF_: u8 = b'n';
const FF_: u8 = b'f';
const CR_: u8 = b'r';
