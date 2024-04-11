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
    variable::{variable::VariableRef, ConceptVariable, ValueVariable, Variable},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Predicate {
    pub predicate: token::Predicate,
    pub value: Value,
}

impl Predicate {
    pub fn new(predicate: token::Predicate, value: Value) -> Self {
        match predicate {
            token::Predicate::EqLegacy => Predicate { predicate: token::Predicate::Eq, value }, // TODO: Deprecate '=' as equality in 3.0
            predicate => Predicate { predicate, value },
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match &self.value {
            Value::ThingVariable(var) => Box::new(iter::once(VariableRef::Concept(var))),
            Value::ValueVariable(var) => Box::new(iter::once(VariableRef::Value(var))),
            _ => Box::new(iter::empty()),
        }
    }
}

impl Validatable for Predicate {
    fn validate(&self) -> Result {
        collect_err([
            validate_string_value_with_substring_predicate(self.predicate, &self.value),
            self.value.validate(),
        ])
    }
}

fn validate_string_value_with_substring_predicate(predicate: token::Predicate, value: &Value) -> Result {
    if predicate.is_substring() && !matches!(value, Value::Constant(Constant::String(_))) {
        Err(TypeQLError::InvalidConstraintPredicate { predicate, value: value.clone() })?
    }
    Ok(())
}

impl fmt::Display for Predicate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.predicate == token::Predicate::Like {
            assert!(matches!(self.value, Value::Constant(Constant::String(_))));
            write!(f, "{} {}", self.predicate, escape_regex(&self.value.to_string()))
        } else if self.predicate == token::Predicate::Eq
            && !(matches!(self.value, Value::ThingVariable(_)) || matches!(self.value, Value::ValueVariable(_)))
        {
            write!(f, "{}", self.value)
        } else {
            write!(f, "{} {}", self.predicate, self.value)
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum Value {
    Constant(Constant),
    ThingVariable(ConceptVariable),
    ValueVariable(ValueVariable),
}

impl Eq for Value {} // can't derive, because floating point types do not implement Eq

impl Validatable for Value {
    fn validate(&self) -> Result {
        match &self {
            Self::Constant(constant) => constant.validate(),
            Self::ThingVariable(variable) => variable.validate(),
            Self::ValueVariable(variable) => variable.validate(),
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
        match variable {
            Variable::Concept(var) => Value::ThingVariable(var),
            Variable::Value(var) => Value::ValueVariable(var),
        }
    }
}

impl From<ConceptVariable> for Value {
    fn from(variable: ConceptVariable) -> Self {
        Value::ThingVariable(variable)
    }
}

impl From<ValueVariable> for Value {
    fn from(variable: ValueVariable) -> Self {
        Value::ValueVariable(variable)
    }
}

impl fmt::Display for Value {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Value::*;
        match self {
            Constant(constant) => write!(f, "{constant}"),
            ThingVariable(var) => write!(f, "{}", var),
            ValueVariable(var) => write!(f, "{}", var),
        }
    }
}
