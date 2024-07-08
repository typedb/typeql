/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{identifier::Identifier, Span},
    pretty::Pretty,
    type_::TypeAny,
};

#[derive(Debug, PartialEq, Eq)]
pub struct Struct {
    span: Option<Span>,
    ident: Identifier,
    fields: Vec<Field>,
}

impl Struct {
    pub fn new(span: Option<Span>, ident: Identifier, fields: Vec<Field>) -> Self {
        Self { span, ident, fields }
    }
}

impl Pretty for Struct {
    fn fmt(&self, _indent_level: usize, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

impl fmt::Display for Struct {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct Field {
    span: Option<Span>,
    key: Identifier,
    type_: TypeAny,
}

impl Field {
    pub fn new(span: Option<Span>, key: Identifier, type_: TypeAny) -> Self {
        Self { span, key, type_ }
    }
}
