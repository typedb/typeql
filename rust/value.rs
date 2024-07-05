/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{Span, Spanned},
    pretty::Pretty,
};

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum Category {
    Boolean,
    Integer,
    Decimal,
    Date,
    DateTime,
    DateTimeTZ,
    Duration,
    String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Literal {
    span: Option<Span>,
    pub category: Option<Category>,
    inner: String, // TODO this can be smarter
}

impl Literal {
    pub(crate) fn new(span: Option<Span>, category: Option<Category>, inner: String) -> Self {
        Self { span, category, inner }
    }

    pub fn parse_to_string(&self) -> String {
        // TODO Result
        assert!(!self.category.is_some_and(|cat| cat != Category::String), "{:?}", self.category);
        parse_string(&self.inner)
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

fn parse_string(escaped_string: &str) -> String {
    // 1. unquote
    let escaped_string = &escaped_string[1..escaped_string.len() - 1]; // TODO check it's quoted

    // 2. JSON unescape
    let mut buf = String::with_capacity(escaped_string.len());

    let mut rest = escaped_string;
    while !rest.is_empty() {
        let (char, escaped_len) = if rest.as_bytes()[0] == b'\\' {
            let bytes = rest.as_bytes();
            assert!(bytes.len() > 1, "TODO: handle improperly escaped string");
            match bytes[1] {
                BSP => ('\x08', 2),
                TAB => ('\x09', 2),
                LF_ => ('\x0a', 2),
                FF_ => ('\x0c', 2),
                CR_ => ('\x0d', 2),
                c @ (b'"' | b'\'' | b'\\') => (c as char, 2),
                b'u' => todo!("Unicode escape handling"),
                other => panic!("unexpected escape character (this should be an Err(_))"),
            }
        } else {
            let char = rest.chars().next().expect("string is non-empty");
            (char, char.len_utf8())
        };
        buf.push(char);
        rest = &rest[escaped_len..];
    }
    buf
}

const HEX: u8 = 0;
const BSP: u8 = b'b';
const TAB: u8 = b't';
const LF_: u8 = b'n';
const FF_: u8 = b'f';
const CR_: u8 = b'r';

const ASCII_CONTROL: usize = 0x20;

const ESCAPE: [u8; ASCII_CONTROL] = [
    HEX, HEX, HEX, HEX, HEX, HEX, HEX, HEX, //
    BSP, TAB, LF_, HEX, FF_, CR_, HEX, HEX, //
    HEX, HEX, HEX, HEX, HEX, HEX, HEX, HEX, //
    HEX, HEX, HEX, HEX, HEX, HEX, HEX, HEX, //
];

const HEX_DIGITS: &[u8; 0x10] = b"0123456789abcdef";
