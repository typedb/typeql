/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        string::escape_regex,
        token,
        validatable::Validatable,
        Result,
    },
    pattern::Constant,
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Comparison {
    pub comparator: token::Comparator,
    pub value: Value,
}

impl Comparison {
    pub fn new(predicate: token::Comparator, value: Value) -> Self {
        match predicate {
            token::Comparator::EqLegacy => Comparison { comparator: token::Comparator::Eq, value }, // TODO: Deprecate '=' as equality in 3.0
            predicate => Comparison { comparator: predicate, value },
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        match &self.value {
            Value::ThingVariable(var) => Box::new(iter::once(VariableRef::Concept(var))),
            _ => Box::new(iter::empty()),
        }
    }
}

impl Validatable for Comparison {
    fn validate(&self) -> Result {
        collect_err([
            validate_string_value_with_substring_predicate(self.comparator, &self.value),
            self.value.validate(),
        ])
    }
}

fn validate_string_value_with_substring_predicate(predicate: token::Comparator, value: &Value) -> Result {
    if predicate.is_substring() && !matches!(value, Value::Constant(Constant::String(_))) {
        Err(TypeQLError::InvalidConstraintPredicate { predicate, value: value.clone() })?
    }
    Ok(())
}

impl fmt::Display for Comparison {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.comparator == token::Comparator::Like {
            assert!(matches!(self.value, Value::Constant(Constant::String(_))));
            write!(f, "{} {}", self.comparator, escape_regex(&self.value.to_string()))
        } else if self.comparator == token::Comparator::Eq && !matches!(self.value, Value::ThingVariable(_))
        {
            write!(f, "{}", self.value)
        } else {
            write!(f, "{} {}", self.comparator, self.value)
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum Value {
    Constant(Constant),
    ThingVariable(Variable),
}

impl Eq for Value {} // can't derive, because floating point types do not implement Eq

impl Validatable for Value {
    fn validate(&self) -> Result {
        match &self {
            Self::Constant(constant) => constant.validate(),
            Self::ThingVariable(variable) => variable.validate(),
        }
    }
}

impl<T: Into<Constant>> From<T> for Value {
    fn from(constant: T) -> Self {
        Value::Constant(constant.into())
    }
}

impl From<Variable> for Value {
    fn from(variable: Variable) -> Self {
        Value::ThingVariable(variable)
    }
}

impl fmt::Display for Value {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Value::*;
        match self {
            Constant(constant) => write!(f, "{constant}"),
            ThingVariable(var) => write!(f, "{}", var),
        }
    }
}
