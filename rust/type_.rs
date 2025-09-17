/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

#[cfg(feature = "quine")]
use {polyquine::Quine, proc_macro2::TokenStream};

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::Pretty,
    variable::Variable,
};

#[derive(Clone, Debug, Hash, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct BuiltinValueType {
    pub span: Option<Span>,
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
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Label {
    pub span: Option<Span>,
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
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct ScopedLabel {
    pub span: Option<Span>,
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
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum NamedType {
    Label(Label),
    BuiltinValueType(BuiltinValueType),
}

impl Spanned for NamedType {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Label(inner) => inner.span(),
            Self::BuiltinValueType(inner) => inner.span(),
        }
    }
}

impl fmt::Display for NamedType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::BuiltinValueType(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum NamedTypeAny {
    Simple(NamedType),
    List(NamedTypeList),
    Optional(NamedTypeOptional),
}

impl Spanned for NamedTypeAny {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Simple(named) => named.span(),
            Self::List(list) => list.span(),
            Self::Optional(optional) => optional.span(),
        }
    }
}

impl Pretty for NamedTypeAny {}

impl fmt::Display for NamedTypeAny {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Simple(inner) => fmt::Display::fmt(inner, f),
            Self::List(inner) => fmt::Display::fmt(inner, f),
            Self::Optional(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct NamedTypeList {
    pub span: Option<Span>,
    pub inner: NamedType,
}

impl NamedTypeList {
    pub fn new(span: Option<Span>, inner: NamedType) -> Self {
        Self { span, inner }
    }
}
impl Spanned for NamedTypeList {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for NamedTypeList {}

impl fmt::Display for NamedTypeList {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}[]", self.inner)
    }
}

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct NamedTypeOptional {
    pub span: Option<Span>,
    pub inner: NamedType,
}

impl NamedTypeOptional {
    pub fn new(span: Option<Span>, inner: NamedType) -> Self {
        Self { span, inner }
    }
}
impl Spanned for NamedTypeOptional {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for NamedTypeOptional {}

impl fmt::Display for NamedTypeOptional {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}?", self.inner)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum TypeRef {
    Label(Label), // person OR friendship:friend OR string
    Scoped(ScopedLabel),
    Variable(Variable), // $t
}

impl Spanned for TypeRef {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Label(label) => label.span(),
            Self::Scoped(scoped) => scoped.span(),
            Self::Variable(var) => var.span(),
        }
    }
}

impl Pretty for TypeRef {}

impl fmt::Display for TypeRef {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::Scoped(inner) => fmt::Display::fmt(inner, f),
            Self::Variable(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum TypeRefAny {
    Type(TypeRef),     // person, friendship:friend, or $t
    List(TypeRefList), // person[], friendship:friend[], or $t[]
}

impl Pretty for TypeRefAny {}
impl Spanned for TypeRefAny {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Type(inner) => inner.span(),
            Self::List(inner) => inner.span(),
        }
    }
}

impl fmt::Display for TypeRefAny {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Type(inner) => fmt::Display::fmt(inner, f),
            Self::List(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct TypeRefList {
    pub span: Option<Span>,
    pub inner: TypeRef,
}

impl TypeRefList {
    pub fn new(span: Option<Span>, inner: TypeRef) -> Self {
        Self { span, inner }
    }
}

impl Spanned for TypeRefList {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for TypeRefList {}

impl fmt::Display for TypeRefList {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}[]", self.inner)
    }
}
