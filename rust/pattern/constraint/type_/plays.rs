/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    variable::{Variable, TypeReference},
    Label,
};
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct PlaysConstraint {
    pub role_type: TypeReference,
    pub overridden_role_type: Option<TypeReference>,
}

impl PlaysConstraint {
    pub(crate) fn new(role_type: TypeReference, overridden_role_type: Option<TypeReference>) -> Self {
        PlaysConstraint { role_type, overridden_role_type }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.role_type.variables().chain(self.overridden_role_type.iter().flat_map(|t| t.variables())))
    }
}

impl Validatable for PlaysConstraint {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.role_type.validate()).chain(self.overridden_role_type.iter().map(Validatable::validate)),
        )
    }
}

impl From<Label> for PlaysConstraint {
    fn from(role_type: Label) -> Self {
        PlaysConstraint::from(TypeReference::Label(role_type))
    }
}

impl From<Variable> for PlaysConstraint {
    fn from(role_type: Variable) -> Self {
        PlaysConstraint::from(TypeReference::Variable(role_type))
    }
}

impl From<TypeReference> for PlaysConstraint {
    fn from(role_type: TypeReference) -> Self {
        PlaysConstraint::new(role_type, None)
    }
}

impl From<(&str, &str)> for PlaysConstraint {
    fn from((relation_type, role_name): (&str, &str)) -> Self {
        PlaysConstraint::from((relation_type.to_owned(), role_name.to_owned()))
    }
}

impl From<(String, String)> for PlaysConstraint {
    fn from((relation_type, role_name): (String, String)) -> Self {
        PlaysConstraint::from(Label::from((relation_type, role_name)))
    }
}

impl From<(Label, Label)> for PlaysConstraint {
    fn from((relation_type, role_name): (Label, Label)) -> Self {
        PlaysConstraint::from((TypeReference::Label(relation_type), TypeReference::Label(role_name)))
    }
}

impl From<(Variable, Variable)> for PlaysConstraint {
    fn from((role_type, overridden_role_type): (Variable, Variable)) -> Self {
        PlaysConstraint::from((TypeReference::Variable(role_type), TypeReference::Variable(overridden_role_type)))
    }
}

impl<T, U, V> From<(T, U, V)> for PlaysConstraint
where
    (T, U): Into<Label>,
    V: Into<Label>,
{
    fn from((relation_type, role_type, overridden_role_name): (T, U, V)) -> Self {
        PlaysConstraint::from(((relation_type, role_type).into(), overridden_role_name.into()))
    }
}

impl From<(TypeReference, TypeReference)> for PlaysConstraint {
    fn from((role_type, overridden_role_type): (TypeReference, TypeReference)) -> Self {
        PlaysConstraint::new(role_type, Some(overridden_role_type))
    }
}

impl fmt::Display for PlaysConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Plays, self.role_type)?;
        if let Some(overridden) = &self.overridden_role_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
