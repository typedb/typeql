/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    variable::{variable::VariableRef, Variable, TypeReference},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelatesConstraint {
    pub role_type: TypeReference,
    pub overridden_role_type: Option<TypeReference>,
}

impl RelatesConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.role_type.variables().chain(self.overridden_role_type.iter().flat_map(|v| v.variables())))
    }
}

impl Validatable for RelatesConstraint {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.role_type.validate()).chain(self.overridden_role_type.iter().map(Validatable::validate)),
        )
    }
}

impl From<&str> for RelatesConstraint {
    fn from(role_name: &str) -> Self {
        RelatesConstraint::from(role_name.to_owned())
    }
}

impl From<String> for RelatesConstraint {
    fn from(role_name: String) -> Self {
        RelatesConstraint::from(Label::from(role_name))
    }
}

impl From<Label> for RelatesConstraint {
    fn from(role_name: Label) -> Self {
        RelatesConstraint::from(TypeReference::Label(role_name))
    }
}

impl From<Variable> for RelatesConstraint {
    fn from(role_type: Variable) -> Self {
        RelatesConstraint::from(TypeReference::Variable(role_type))
    }
}

impl From<TypeReference> for RelatesConstraint {
    fn from(role_type: TypeReference) -> Self {
        RelatesConstraint { role_type, overridden_role_type: None }
    }
}

impl From<(&str, &str)> for RelatesConstraint {
    fn from((role_name, overridden_role_name): (&str, &str)) -> Self {
        RelatesConstraint::from((role_name.to_owned(), overridden_role_name.to_owned()))
    }
}

impl From<(String, String)> for RelatesConstraint {
    fn from((role_name, overridden_role_name): (String, String)) -> Self {
        RelatesConstraint::from((Label::from(role_name), Label::from(overridden_role_name)))
    }
}

impl From<(Label, Label)> for RelatesConstraint {
    fn from((role_name, overridden_role_name): (Label, Label)) -> Self {
        RelatesConstraint::from((TypeReference::Label(role_name), TypeReference::Label(overridden_role_name)))
    }
}

impl From<(Variable, Variable)> for RelatesConstraint {
    fn from((role_type, overridden_role_name): (Variable, Variable)) -> Self {
        RelatesConstraint::from((TypeReference::Variable(role_type), TypeReference::Variable(overridden_role_name)))
    }
}

impl From<(TypeReference, TypeReference)> for RelatesConstraint {
    fn from((role_type, overridden_role_name): (TypeReference, TypeReference)) -> Self {
        RelatesConstraint { role_type, overridden_role_type: Some(overridden_role_name) }
    }
}

impl From<(TypeReference, Option<TypeReference>)> for RelatesConstraint {
    fn from((role_type, overridden_role_type): (TypeReference, Option<TypeReference>)) -> Self {
        RelatesConstraint { role_type, overridden_role_type }
    }
}

impl fmt::Display for RelatesConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Relates, self.role_type)?;
        if let Some(overridden) = &self.overridden_role_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
