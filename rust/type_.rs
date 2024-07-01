/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    identifier::{Label, ScopedLabel, Variable},
    pretty::Pretty,
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

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::ScopedLabel(inner) => fmt::Display::fmt(inner, f),
            Self::Variable(inner) => todo!(),
            Self::BuiltinValue(inner) => fmt::Display::fmt(inner, f),
        }
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
