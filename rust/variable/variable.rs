/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use std::{fmt, fmt::Formatter, hash::Hash};

use crate::{
    common::{error::TypeQLError, validatable::Validatable, Result},
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

    pub fn is_name(&self) -> bool {
        match self {
            Variable::Concept(var) => var.is_name(),
            Variable::Value(var) => var.is_name(),
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
            VariableRef::Concept(var) => (*var).is_name(),
            VariableRef::Value(var) => (*var).is_name(),
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
    // TODO this should be a static regex
    if !is_valid_variable_name(name) {
        Err(TypeQLError::InvalidVariableName { name: name.to_owned() })?
    }
    Ok(())
}

pub(crate) fn is_valid_variable_name(name: &str) -> bool {
    name.starts_with(|c: char| c.is_ascii_alphanumeric())
        && name.chars().all(|c| c.is_ascii_alphanumeric() || c == '_' || c == '-')
}
