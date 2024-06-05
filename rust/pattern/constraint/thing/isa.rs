/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    pattern::IsExplicit,
};
use crate::pattern::Label;
use crate::variable::{Variable, TypeReference};
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_reference: TypeReference,
    pub is_explicit: IsExplicit,
}

impl IsaConstraint {
    fn new(type_reference: TypeReference, is_explicit: IsExplicit) -> Self {
        IsaConstraint { type_reference, is_explicit }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.type_reference.variables()
    }
}

impl Validatable for IsaConstraint {
    fn validate(&self) -> Result {
        self.type_reference.validate()
    }
}

impl<T: Into<Label>> From<T> for IsaConstraint {
    fn from(label: T) -> Self {
        IsaConstraint::new(TypeReference::Label(label.into()), IsExplicit::No)
    }
}

impl<T: Into<Label>> From<(T, IsExplicit)> for IsaConstraint {
    fn from((label, is_explicit): (T, IsExplicit)) -> Self {
        IsaConstraint::new(TypeReference::Label(label.into()), is_explicit)
    }
}

impl From<Variable> for IsaConstraint {
    fn from(var: Variable) -> Self {
        IsaConstraint::new(TypeReference::Variable(var), IsExplicit::No)
    }
}

impl From<(Variable, IsExplicit)> for IsaConstraint {
    fn from((var, is_explicit): (Variable, IsExplicit)) -> Self {
        IsaConstraint::new(TypeReference::Variable(var), is_explicit)
    }
}

impl fmt::Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{} {}",
            match self.is_explicit {
                IsExplicit::Yes => token::Constraint::IsaX,
                IsExplicit::No => token::Constraint::Isa,
            },
            self.type_reference
        )
    }
}
