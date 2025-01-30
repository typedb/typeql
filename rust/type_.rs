/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::Pretty,
    variable::Variable,
};

#[derive(Clone, Debug, Hash, Eq, PartialEq)]
pub struct BuiltinValueType {
    span: Option<Span>,
    pub token: token::ValueType,
}

impl BuiltinValueType {
    pub fn new(span: Option<Span>, token: token::ValueType) -> Self {
        Self { span, token }
    }
}

impl Spanned for BuiltinValueType {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for BuiltinValueType {}

impl fmt::Display for BuiltinValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.token)
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub struct Label {
    span: Option<Span>,
    pub ident: Identifier,
}

impl Label {
    pub fn new(span: Option<Span>, ident: Identifier) -> Self {
        Self { span, ident }
    }
}

impl Spanned for Label {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Label {}

impl fmt::Display for Label {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.ident)
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub struct ScopedLabel {
    span: Option<Span>,
    pub scope: Label,
    pub name: Label,
}

impl ScopedLabel {
    pub fn new(span: Option<Span>, scope: Label, name: Label) -> Self {
        Self { span, scope, name }
    }
}

impl Spanned for ScopedLabel {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for ScopedLabel {}

impl fmt::Display for ScopedLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}:{}", self.scope, self.name)
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
pub enum NamedType {
    Label(Label),
    Role(ScopedLabel),
    BuiltinValueType(BuiltinValueType),
}

impl Spanned for NamedType {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Label(inner) => inner.span(),
            Self::BuiltinValueType(inner) => inner.span(),
            Self::Role(inner) => inner.span(),
        }
    }
}

impl fmt::Display for NamedType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::BuiltinValueType(inner) => fmt::Display::fmt(inner, f),
            Self::Role(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeRef {
    Named(NamedType),   // person OR friendship:friend OR string
    Variable(Variable), // $t
}

impl Spanned for TypeRef {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Named(named) => named.span(),
            Self::Variable(var) => var.span(),
        }
    }
}

impl Pretty for TypeRef {}

impl fmt::Display for TypeRef {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Named(inner) => fmt::Display::fmt(inner, f),
            Self::Variable(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeRefAny {
    Type(TypeRef),      // person, friendship:friend, or $t
    Optional(Optional), // person?, friendship:friend?, or $t?
    List(List),         // person[], friendship:friend[], or $t[]
}

impl Pretty for TypeRefAny {}
impl Spanned for TypeRefAny {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Type(inner) => inner.span(),
            Self::Optional(inner) => inner.span(),
            Self::List(inner) => inner.span(),
        }
    }
}

impl fmt::Display for TypeRefAny {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Type(inner) => fmt::Display::fmt(inner, f),
            Self::Optional(inner) => fmt::Display::fmt(inner, f),
            Self::List(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Optional {
    span: Option<Span>,
    pub inner: TypeRef,
}

impl Optional {
    pub fn new(span: Option<Span>, inner: TypeRef) -> Self {
        Self { span, inner }
    }
}
impl Spanned for Optional {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Optional {}

impl fmt::Display for Optional {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}?", self.inner)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct List {
    span: Option<Span>,
    pub inner: TypeRef,
}

impl List {
    pub fn new(span: Option<Span>, inner: TypeRef) -> Self {
        Self { span, inner }
    }
}

impl Spanned for List {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for List {}

impl fmt::Display for List {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}[]", self.inner)
    }
}
