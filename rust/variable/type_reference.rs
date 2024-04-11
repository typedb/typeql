/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, fmt::Formatter, iter};

use crate::{
    common::validatable::Validatable,
    pattern::{Label, TypeStatement, TypeStatementBuilder},
    variable::{variable::VariableRef, ConceptVariable},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeReference {
    Label(Label),
    Variable(ConceptVariable),
}

impl TypeReference {
    pub fn into_type_statement(self) -> TypeStatement {
        match self {
            Self::Label(label) => ConceptVariable::Hidden.type_(label),
            Self::Variable(var) => var.into(),
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            TypeReference::Label(_) => Box::new(iter::empty()),
            TypeReference::Variable(var) => Box::new(iter::once(VariableRef::Concept(var))),
        }
    }
}

impl Validatable for TypeReference {
    fn validate(&self) -> crate::common::Result {
        match self {
            TypeReference::Label(label) => label.validate(),
            TypeReference::Variable(var) => var.validate(),
        }
    }
}

impl fmt::Display for TypeReference {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            TypeReference::Label(label) => write!(f, "{}", label),
            TypeReference::Variable(var) => write!(f, "{}", var),
        }
    }
}
