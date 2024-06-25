/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use self::declaration::{Owns, Plays, Relates, Sub, ValueType};
use crate::{
    common::{Span, Spanned},
    identifier::Identifier,
    write_joined,
};

pub mod declaration;

#[derive(Debug, Clone, Eq, PartialEq)]
// TODO name?
pub enum TypeTrait {
    Sub(Sub),
    Owns(Owns),
    Plays(Plays),
    Relates(Relates),
    ValueType(ValueType),
}

impl fmt::Display for TypeTrait {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Sub(inner) => fmt::Display::fmt(inner, f),
            Self::Owns(inner) => fmt::Display::fmt(inner, f),
            Self::Plays(inner) => fmt::Display::fmt(inner, f),
            Self::Relates(inner) => fmt::Display::fmt(inner, f),
            Self::ValueType(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Type {
    span: Option<Span>,
    label: Identifier,
    traits: Vec<TypeTrait>,
}

impl Type {
    pub fn new(span: Option<Span>, label: Identifier, traits: Vec<TypeTrait>) -> Self {
        Self { span, label, traits }
    }
}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        f.write_char(' ')?;
        let joiner = if f.alternate() { ",\n    " } else { ", " };
        write_joined!(f, joiner, &self.traits)?;
        Ok(())
    }
}

impl Spanned for Type {
    fn span(&self) -> Option<Span> {
        self.span
    }
}
