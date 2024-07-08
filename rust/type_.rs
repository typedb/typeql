/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::Pretty,
    schema,
    variable::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct BuiltinValueType {
    span: Option<Span>,
    name: token::ValueType,
}

impl BuiltinValueType {
    pub fn new(span: Option<Span>, name: token::ValueType) -> Self {
        Self { span, name }
    }
}

impl Pretty for BuiltinValueType {}

impl fmt::Display for BuiltinValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.name)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Type {
    Label(Label),                   // person
    ScopedLabel(ScopedLabel),       // friendship:friend
    Variable(Variable),             // $t
    BuiltinValue(BuiltinValueType), // string
}

impl Pretty for Type {}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::ScopedLabel(inner) => fmt::Display::fmt(inner, f),
            Self::Variable(inner) => fmt::Display::fmt(inner, f),
            Self::BuiltinValue(inner) => fmt::Display::fmt(inner, f),
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

impl Label {
    pub fn as_str(&self) -> &str {
        match self {
            Label::Identifier(ident) => ident.as_str(),
            Label::Reserved(reserved) => reserved.token.as_str(),
        }
    }
}

impl From<Label> for schema::definable::Type {
    fn from(label: Label) -> Self {
        Self::build(label)
    }
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

impl Pretty for ScopedLabel {}

impl fmt::Display for ScopedLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}:{}", self.scope, self.name)
    }
}
#[derive(Debug, Clone, Eq, PartialEq)]
// TODO name?
pub enum TypeAny {
    Type(Type),         // person, friendship:friend, or $t
    Optional(Optional), // person?, friendship:friend?, or $t?
    List(List),         // person[], friendship:friend[], or $t[]
}

impl Pretty for TypeAny {}

impl fmt::Display for TypeAny {
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
    inner: Type,
}

impl Optional {
    pub fn new(span: Option<Span>, inner: Type) -> Self {
        Self { span, inner }
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
    inner: Type,
}

impl List {
    pub fn new(span: Option<Span>, inner: Type) -> Self {
        Self { span, inner }
    }
}

impl Pretty for List {}

impl fmt::Display for List {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}[]", self.inner)
    }
}
