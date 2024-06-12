/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::pattern::Label;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeDeclaration {
    pub label: Label,
    // pub sub: Option<SubConstraint>,
    // pub value_type: Option<ValueTypeConstraint>,
    // pub owns: Vec<OwnsConstraint>,
    // pub plays: Vec<PlaysConstraint>,
    // pub relates: Vec<RelatesConstraint>,
}

impl TypeDeclaration {
    pub fn new(label: Label) -> TypeDeclaration {
        TypeDeclaration { label }
    }
}

impl fmt::Display for TypeDeclaration {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        Ok(())
    }
}
