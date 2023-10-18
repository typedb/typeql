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

use std::{fmt, fmt::Formatter};

use crate::{
    common::{validatable::Validatable, Result},
    pattern::{Reference},
    variable::{ConceptVariable, ValueVariable},
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum Variable {
    Concept(ConceptVariable),
    Value(ValueVariable),
}

impl Variable {
    pub fn reference(&self) -> &Reference {
        match self {
            Variable::Concept(concept_variable) => &concept_variable.reference,
            Variable::Value(value_variable) => &value_variable.reference,
        }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        match self {
            Variable::Concept(concept_variable) => concept_variable.references(),
            Variable::Value(value_variable) => value_variable.references(),
        }
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

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            Variable::Concept(concept_variable) => write!(f, "{concept_variable}"),
            Variable::Value(value_variable) => write!(f, "{value_variable}"),
        }
    }
}
