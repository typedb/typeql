/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{define::Define, redefine::Redefine, undefine::Undefine};
use crate::common::{Span, Spanned};

mod define;
mod redefine;
mod undefine;

#[derive(Debug, Eq, PartialEq)]
pub enum SchemaQuery {
    Define(Define),
    Redefine(Redefine),
    Undefine(Undefine),
}

impl Spanned for SchemaQuery {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Define(inner) => inner.span(),
            Self::Redefine(inner) => inner.span(),
            Self::Undefine(inner) => inner.span(),
        }
    }
}

impl fmt::Display for SchemaQuery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Define(inner) => fmt::Display::fmt(inner, f),
            Self::Redefine(inner) => fmt::Display::fmt(inner, f),
            Self::Undefine(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
