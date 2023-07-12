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

use std::fmt;

use crate::{
    builder::var_concept,
    common::{token, validatable::Validatable, Result},
    pattern::{ConceptVariable, UnboundConceptVariable},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsConstraint {
    pub variable: Box<ConceptVariable>,
}

impl IsConstraint {
    fn new(var: ConceptVariable) -> Self {
        Self { variable: Box::new(var) }
    }
}

impl Validatable for IsConstraint {
    fn validate(&self) -> Result<()> {
        self.variable.validate()
    }
}

impl From<&str> for IsConstraint {
    fn from(string: &str) -> Self {
        Self::from(var_concept(string))
    }
}

impl From<String> for IsConstraint {
    fn from(string: String) -> Self {
        Self::from(var_concept(string))
    }
}

impl From<UnboundConceptVariable> for IsConstraint {
    fn from(var: UnboundConceptVariable) -> Self {
        Self::new(var.into_concept())
    }
}

impl fmt::Display for IsConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Is, self.variable)
    }
}
