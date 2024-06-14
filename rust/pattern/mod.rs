/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{definition::Type, label::Label};
use crate::{enum_getter, enum_wrapper};

pub(crate) mod definition;
pub mod label;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Definable {
    TypeDeclaration(Type),
}

enum_getter! { Definable
    into_type_statement(TypeDeclaration) => Type,
}

enum_wrapper! { Definable
    Type => TypeDeclaration,
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::TypeDeclaration(declaration) => fmt::Display::fmt(declaration, f),
        }
    }
}
