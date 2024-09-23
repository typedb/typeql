/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, fmt::Formatter};

use crate::{
    common::{identifier::Identifier, Span},
    pretty::{indent, Pretty},
    token,
    type_::TypeRefAny,
};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Struct {
    span: Option<Span>,
    pub ident: Identifier,
    pub fields: Vec<Field>,
}

impl Struct {
    pub fn new(span: Option<Span>, ident: Identifier, fields: Vec<Field>) -> Self {
        Self { span, ident, fields }
    }
}

impl Pretty for Struct {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        writeln!(f, "{} {}{}", token::Keyword::Struct, self.ident, token::Char::Colon)?;
        if !self.fields.is_empty() {
            Pretty::fmt(&self.fields[0], indent_level + 1, f)?;
            for field in &self.fields[1..] {
                Pretty::fmt(field, indent_level + 1, f)?;
            }
            writeln!(f)?;
        }
        writeln!(f, "{}", token::Char::Semicolon)
    }
}

impl fmt::Display for Struct {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}{}", token::Keyword::Struct, self.ident, token::Char::Colon)?;
        if !self.fields.is_empty() {
            write!(f, "{}", self.fields[0])?;
            for field in &self.fields[1..] {
                write!(f, ", {}", field)?;
            }
        }
        write!(f, "{}", token::Char::Semicolon)
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Field {
    span: Option<Span>,
    pub key: Identifier,
    pub type_: TypeRefAny,
}

impl Field {
    pub fn new(span: Option<Span>, key: Identifier, type_: TypeRefAny) -> Self {
        Self { span, key, type_ }
    }
}

impl Pretty for Field {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        fmt::Display::fmt(self, f)
    }
}

impl fmt::Display for Field {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.key, token::Keyword::Value, self.type_)
    }
}
