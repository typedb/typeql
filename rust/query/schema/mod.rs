/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{define::Define, undefine::Undefine};
use crate::common::{Span, Spanned};

mod define;

mod undefine;

#[derive(Debug, Eq, PartialEq)]
pub enum SchemaQuery {
    Define(Define),
    Undefine(Undefine),
}

impl Spanned for SchemaQuery {
    fn span(&self) -> Option<Span> {
        match self {
            SchemaQuery::Define(define) => define.span(),
            SchemaQuery::Undefine(undefine) => undefine.span(),
        }
    }
}

impl fmt::Display for SchemaQuery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Define(define_query) => fmt::Display::fmt(define_query, f),
            Self::Undefine(undefine_query) => fmt::Display::fmt(undefine_query, f),
        }
    }
}
