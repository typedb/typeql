/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::Span,
    identifier::{Identifier, Label},
    pretty::Pretty,
};

#[derive(Debug, PartialEq, Eq)]
pub struct Struct {
    span: Option<Span>,
    fields: Vec<Field>,
}

impl Struct {
    pub fn new(span: Option<Span>, fields: Vec<Field>) -> Self {
        Self { span, fields }
    }
}

impl Pretty for Struct {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
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
    key: Identifier,
    type_: Label,
}

impl Field {
    pub fn new(key: Identifier, type_: Label) -> Self {
        Self { key, type_ }
    }
}
