/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, fmt::Formatter, hash::Hash};

use crate::{
    common::{error::TypeQLError, identifier::is_valid_var_identifier, validatable::Validatable, Result},
    variable::{ConceptVariable, ValueVariable},
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum Variable {
    Concept(ConceptVariable),
    Value(ValueVariable),
}

impl Variable {
    pub fn as_ref(&self) -> VariableRef<'_> {
        match self {
            Self::Concept(cv) => VariableRef::Concept(cv),
            Self::Value(vv) => VariableRef::Value(vv),
        }
    }

    pub fn is_named(&self) -> bool {
        match self {
            Variable::Concept(var) => var.is_named(),
            Variable::Value(var) => var.is_named(),
        }
    }
}

impl From<ConceptVariable> for Variable {
    fn from(concept: ConceptVariable) -> Self {
        Variable::Concept(concept)
    }
}

impl From<ValueVariable> for Variable {
    fn from(value: ValueVariable) -> Self {
        Variable::Value(value)
    }
}

impl Validatable for Variable {
    fn validate(&self) -> Result {
        match self {
            Variable::Concept(concept_variable) => concept_variable.validate(),
            Variable::Value(value_variable) => value_variable.validate(),
        }
    }
}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            Variable::Concept(concept_variable) => write!(f, "{concept_variable}"),
            Variable::Value(value_variable) => write!(f, "{value_variable}"),
        }
    }
}

#[derive(Debug, Clone, Copy, Eq, Hash, PartialEq)]
pub enum VariableRef<'a> {
    Concept(&'a ConceptVariable),
    Value(&'a ValueVariable),
}

impl VariableRef<'_> {
    pub fn is_name(&self) -> bool {
        match self {
            VariableRef::Concept(var) => (*var).is_named(),
            VariableRef::Value(var) => (*var).is_named(),
        }
    }

    pub fn is_concept(&self) -> bool {
        matches!(self, VariableRef::Concept(_))
    }

    pub fn is_value(&self) -> bool {
        matches!(self, VariableRef::Value(_))
    }

    pub fn to_owned(self) -> Variable {
        match self {
            Self::Concept(var) => Variable::Concept((*var).clone()),
            Self::Value(var) => Variable::Value((*var).clone()),
        }
    }
}

impl fmt::Display for VariableRef<'_> {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            VariableRef::Concept(var) => write!(f, "{}", *var),
            VariableRef::Value(var) => write!(f, "{}", *var),
        }
    }
}

pub(crate) fn validate_variable_name(name: &str) -> Result {
    if !is_valid_var_identifier(name) {
        Err(TypeQLError::InvalidVariableName { name: name.to_owned() })?
    }
    Ok(())
}
