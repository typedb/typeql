/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

#[cfg(feature = "quine")]
use {polyquine::Quine, proc_macro2::TokenStream};

pub use self::{function::Function, struct_::Struct, type_::Type};
use crate::pretty::Pretty;

pub mod function;
pub mod struct_;
pub mod type_;

#[derive(Debug, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum Definable {
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
            Self::TypeDeclaration(declaration) => Pretty::fmt(declaration, indent_level, f),
            Self::Function(declaration) => Pretty::fmt(declaration, indent_level, f),
            Self::Struct(declaration) => Pretty::fmt(declaration, indent_level, f),
        }
    }
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::TypeDeclaration(declaration) => fmt::Display::fmt(declaration, f),
            Self::Function(declaration) => fmt::Display::fmt(declaration, f),
            Self::Struct(declaration) => fmt::Display::fmt(declaration, f),
        }
    }
}
