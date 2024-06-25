/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::common::{token, Span, Spanned};

#[derive(Debug, Clone, Eq, PartialEq, Hash)]
pub struct Identifier {
    span: Option<Span>,
    ident: String,
}

impl Identifier {
    pub fn new(span: Option<Span>, ident: String) -> Self {
        Self { span, ident }
    }
}

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

// FIXME move
#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Anonymous(Option<Span>),
    Named(Option<Span>, Identifier),
}

impl fmt::Display for Variable {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

impl Spanned for Variable {
    fn span(&self) -> Option<Span> {
        match self {
            Variable::Anonymous(span) | Variable::Named(span, _) => *span,
        }
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub struct ReservedLabel {
    span: Option<Span>,
    token: token::Type,
}

impl ReservedLabel {
    pub fn new(span: Option<Span>, token: token::Type) -> Self {
        Self { span, token }
    }
}

impl fmt::Display for ReservedLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.token, f)
    }
}

impl Spanned for ReservedLabel {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub enum Label {
    Identifier(Identifier),
    Reserved(ReservedLabel),
}

impl Spanned for Label {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Identifier(inner) => inner.span(),
            Self::Reserved(inner) => inner.span(),
        }
    }
}

impl fmt::Display for Label {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Identifier(inner) => fmt::Display::fmt(inner, f),
            Self::Reserved(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub struct ScopedLabel {
    span: Option<Span>,
    scope: Label,
    name: Label,
}

impl ScopedLabel {
    pub fn new(span: Option<Span>, scope: Label, name: Label) -> Self {
        Self { span, scope, name }
    }
}

impl fmt::Display for ScopedLabel {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

