/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{function::Function, struct_::Struct, type_::Type};
use crate::{
    Label, ScopedLabel,
    common::{Span, Spanned},
    pretty::Pretty,
    token,
};

pub mod function;
pub mod struct_;
pub mod type_;

#[derive(Debug, Eq, PartialEq)]
pub enum Definable {
    TypeRename(TypeRename),
    RoleTypeRename(RoleTypeRename),
    TypeDeclaration(Type),
    Function(Function),
    Struct(Struct),
}

impl From<Type> for Definable {
    fn from(type_: Type) -> Self {
        Self::TypeDeclaration(type_)
    }
}

impl Pretty for Definable {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::TypeRename(declaration) => Pretty::fmt(declaration, indent_level, f),
            Self::RoleTypeRename(declaration) => Pretty::fmt(declaration, indent_level, f),
            Self::TypeDeclaration(declaration) => Pretty::fmt(declaration, indent_level, f),
            Self::Function(declaration) => Pretty::fmt(declaration, indent_level, f),
            Self::Struct(declaration) => Pretty::fmt(declaration, indent_level, f),
        }
    }
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::TypeRename(declaration) => fmt::Display::fmt(declaration, f),
            Self::RoleTypeRename(declaration) => fmt::Display::fmt(declaration, f),
            Self::TypeDeclaration(declaration) => fmt::Display::fmt(declaration, f),
            Self::Function(declaration) => fmt::Display::fmt(declaration, f),
            Self::Struct(declaration) => fmt::Display::fmt(declaration, f),
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct TypeRename {
    pub span: Option<Span>,
    pub kind: Option<token::Kind>,
    pub from: Label,
    pub to: Label,
}

impl Spanned for TypeRename {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for TypeRename {
    fn fmt(&self, _indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(kind) = &self.kind {
            write!(f, "{} ", kind)?;
        }
        write!(f, "{} {} {};", self.from, token::Keyword::Label, self.to)?;
        Ok(())
    }
}

impl fmt::Display for TypeRename {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(kind) = &self.kind {
            write!(f, "{} ", kind)?;
        }
        write!(f, "{} {} {};", self.from, token::Keyword::Label, self.to)?;
        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct RoleTypeRename {
    pub span: Option<Span>,
    pub from: ScopedLabel,
    pub to: Label,
}

impl Spanned for RoleTypeRename {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for RoleTypeRename {
    fn fmt(&self, _indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {};", self.from, token::Keyword::Label, self.to)
    }
}

impl fmt::Display for RoleTypeRename {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {};", self.from, token::Keyword::Label, self.to)
    }
}
