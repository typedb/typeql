/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, validatable::Validatable, Result},
    pattern::constraint::IsConstraint,
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ConceptStatement {
    pub variable: Variable,
    pub is_constraint: Option<IsConstraint>,
}

impl ConceptStatement {
    pub fn new(variable: Variable) -> ConceptStatement {
        ConceptStatement { variable, is_constraint: None }
    }

    pub fn owner(&self) -> VariableRef<'_> {
        VariableRef::Concept(&self.variable)
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(iter::once(self.owner()).chain(self.is_constraint.iter().map(|is| VariableRef::Concept(&is.variable))))
    }

    pub fn constrain_is(self, is: IsConstraint) -> ConceptStatement {
        Self { is_constraint: Some(is), ..self }
    }
}

impl Validatable for ConceptStatement {
    fn validate(&self) -> Result {
        collect_err(iter::once(self.variable.validate()).chain(self.is_constraint.iter().map(Validatable::validate)))
    }
}

impl From<Variable> for ConceptStatement {
    fn from(variable: Variable) -> Self {
        ConceptStatement::new(variable)
    }
}

impl fmt::Display for ConceptStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(is) = &self.is_constraint {
            write!(f, " {is}")?;
        }
        Ok(())
    }
}
