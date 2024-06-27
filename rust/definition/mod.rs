/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{function::Function, struct_::Struct, type_::Type};

pub mod function;
pub mod struct_;
pub mod type_;

#[derive(Debug, Eq, PartialEq)]
pub enum Definable {
    TypeDeclaration(Type),
    Function(Function),
    Struct(Struct),
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

impl From<Type> for Definable {
    fn from(type_: Type) -> Self {
        Self::TypeDeclaration(type_)
    }
}
