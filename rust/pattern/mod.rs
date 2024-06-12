/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{label::Label, statement::TypeDeclaration};
use crate::{enum_getter, enum_wrapper};

pub mod label;
pub(crate) mod statement;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Definable {
    TypeDeclaration(TypeDeclaration),
}

enum_getter! { Definable
    into_type_statement(TypeDeclaration) => TypeDeclaration,
}

enum_wrapper! { Definable
    TypeDeclaration => TypeDeclaration,
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::TypeDeclaration(declaration) => fmt::Display::fmt(declaration, f),
        }
    }
}
