/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    pattern::IsExplicit,
    variable::{variable::VariableRef, Variable, TypeReference},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SubConstraint {
    pub type_: TypeReference,
    pub is_explicit: IsExplicit,
}

impl SubConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.type_.variables()
    }
}

impl Validatable for SubConstraint {
    fn validate(&self) -> Result {
        self.type_.validate()
    }
}

impl<T: Into<Label>> From<T> for SubConstraint {
    fn from(type_: T) -> Self {
        Self::from(TypeReference::Label(type_.into()))
    }
}

impl From<Variable> for SubConstraint {
    fn from(type_: Variable) -> Self {
        Self::from(TypeReference::Variable(type_))
    }
}

impl From<TypeReference> for SubConstraint {
    fn from(type_: TypeReference) -> Self {
        SubConstraint { type_, is_explicit: IsExplicit::No }
    }
}

impl<T: Into<Label>> From<(T, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (T, IsExplicit)) -> Self {
        Self::from((TypeReference::Label(type_.into()), is_explicit))
    }
}

impl From<(Variable, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (Variable, IsExplicit)) -> Self {
        Self::from((TypeReference::Variable(type_), is_explicit))
    }
}

impl From<(TypeReference, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (TypeReference, IsExplicit)) -> Self {
        Self { type_, is_explicit }
    }
}

impl fmt::Display for SubConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{} {}",
            match self.is_explicit {
                IsExplicit::Yes => token::Constraint::SubX,
                IsExplicit::No => token::Constraint::Sub,
            },
            self.type_
        )
    }
}
