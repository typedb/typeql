/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    variable::ConceptVariable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsConstraint {
    pub variable: ConceptVariable,
}

impl Validatable for IsConstraint {
    fn validate(&self) -> Result {
        self.variable.validate()
    }
}

impl From<&str> for IsConstraint {
    fn from(string: &str) -> Self {
        Self::from(ConceptVariable::Named(string.to_string()))
    }
}

impl From<String> for IsConstraint {
    fn from(string: String) -> Self {
        Self::from(ConceptVariable::Named(string))
    }
}

impl From<ConceptVariable> for IsConstraint {
    fn from(variable: ConceptVariable) -> Self {
        Self { variable }
    }
}

impl fmt::Display for IsConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Is, self.variable)
    }
}
