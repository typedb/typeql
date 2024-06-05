/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use chrono::NaiveDateTime;
pub use function::Function;
pub use operation::Operation;

use crate::{
    pattern::Constant,
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

pub mod builder;
mod function;
mod operation;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Expression {
    Operation(Operation),
    Function(Function),
    Constant(Constant),
    ThingVariable(Variable),
}

impl fmt::Display for Expression {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Expression::Operation(operation) => write!(f, "{operation}"),
            Expression::Function(function) => write!(f, "{function}"),
            Expression::Constant(constant) => write!(f, "{constant}"),
            Expression::ThingVariable(variable) => write!(f, "{variable}"),
        }
    }
}

impl Expression {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            Expression::Operation(operation) => operation.variables(),
            Expression::Function(function) => function.variables(),
            Expression::Constant(_constant) => Box::new(iter::empty()),
            Expression::ThingVariable(variable) => Box::new(iter::once(VariableRef::Concept(variable))),
        }
    }
}

impl From<Operation> for Expression {
    fn from(operation: Operation) -> Self {
        Self::Operation(operation)
    }
}

impl From<Function> for Expression {
    fn from(function: Function) -> Self {
        Self::Function(function)
    }
}

impl From<Constant> for Expression {
    fn from(constant: Constant) -> Self {
        Self::Constant(constant)
    }
}

impl From<Variable> for Expression {
    fn from(variable: Variable) -> Self {
        Self::ThingVariable(variable)
    }
}

impl From<i64> for Expression {
    fn from(value: i64) -> Self {
        Self::Constant(value.into())
    }
}

impl From<f64> for Expression {
    fn from(value: f64) -> Self {
        Self::Constant(value.into())
    }
}

impl From<bool> for Expression {
    fn from(value: bool) -> Self {
        Self::Constant(value.into())
    }
}

impl From<String> for Expression {
    fn from(value: String) -> Self {
        Self::Constant(value.into())
    }
}

impl From<&str> for Expression {
    fn from(value: &str) -> Self {
        Self::Constant(value.into())
    }
}

impl From<NaiveDateTime> for Expression {
    fn from(value: NaiveDateTime) -> Self {
        Self::Constant(value.into())
    }
}
