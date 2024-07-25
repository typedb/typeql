/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{
    fmt,
    fmt::{Debug, Formatter},
};
use std::fmt::Write;
use std::path::Display;

use crate::{
    common::{error::TypeQLError, Span, Spanned},
    pretty::Pretty,
    Result,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct BooleanLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct StringLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IntegerLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Sign {
    Plus,
    Minus,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SignedIntegerLiteral {
    pub sign: Sign,
    pub integral: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SignedDecimalLiteral {
    pub sign: Sign,
    pub decimal: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateFragment {
    pub year: String,
    pub month: String,
    pub day: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TimeFragment {
    pub hour: String,
    pub minute: String,
    pub second: Option<String>,
    pub second_fraction: Option<String>,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateTimeTZLiteral {
    pub date: DateFragment,
    pub time: TimeFragment,
    pub timezone: TimeZone,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateTimeLiteral {
    pub date: DateFragment,
    pub time: TimeFragment,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateLiteral {
    pub date: DateFragment,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TimeZone {
    IANA(String, String),
    ISO(String),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DurationLiteral {
    pub date: Option<DurationDate>,
    pub time: Option<DurationTime>,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct StructLiteral {
    // TODO
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum DurationDate {
    Years(String),
    Months(String),
    Weeks(String),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum DurationTime {
    Days(String),
    Hours(String),
    Minutes(String),
    Seconds(String),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ValueLiteral {
    Boolean(BooleanLiteral),
    Integer(SignedIntegerLiteral),
    Decimal(SignedDecimalLiteral),
    Date(DateLiteral),
    DateTime(DateTimeLiteral),
    DateTimeTz(DateTimeTZLiteral),
    Duration(DurationLiteral),
    String(StringLiteral),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Literal {
    span: Option<Span>,
    pub inner: ValueLiteral,
}

impl Literal {
    pub(crate) fn new(span: Option<Span>, inner: ValueLiteral) -> Self {
        Self { span, inner }
    }
}

impl Spanned for Literal {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Literal {}

impl fmt::Display for Literal {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        self.inner.fmt(f)
    }
}

impl fmt::Display for IntegerLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.value.as_str())
    }
}

impl fmt::Display for StringLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.value.as_str())
    }
}

impl fmt::Display for Sign {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            Sign::Plus => f.write_char('+'),
            Sign::Minus => f.write_char('-'),
        }
    }
}

impl fmt::Display for SignedDecimalLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        std::fmt::Display::fmt(&self.sign, f)?;
        f.write_str(self.decimal.as_str())?;
        Ok(())
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
