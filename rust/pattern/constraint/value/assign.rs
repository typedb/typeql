/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, validatable::Validatable, Result},
    pattern::Expression,
    variable::variable::VariableRef,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct AssignConstraint {
    pub expression: Expression,
}

impl AssignConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.expression.variables()
    }
}

impl Validatable for AssignConstraint {
    fn validate(&self) -> Result {
        Ok(())
    }
}

impl<T: Into<Expression>> From<T> for AssignConstraint {
    fn from(expr: T) -> Self {
        Self { expression: expr.into() }
    }
}

impl fmt::Display for AssignConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Assign, self.expression)
    }
}
