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
    common::{Result, token, validatable::Validatable},
    Label,
    pattern::IsExplicit,
    variable::ConceptVariable,
};
use crate::variable::TypeReference;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_reference: TypeReference,
    pub is_explicit: IsExplicit,
}

impl IsaConstraint {
    fn new(type_reference: TypeReference, is_explicit: IsExplicit) -> Self {
        IsaConstraint { type_reference, is_explicit }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        self.type_reference.variables()
    }
}

impl Validatable for IsaConstraint {
    fn validate(&self) -> Result {
        self.type_reference.validate()
    }
}

impl<T: Into<Label>> From<T> for IsaConstraint {
    fn from(label: T) -> Self {
        IsaConstraint::new(TypeReference::Label(label.into()), IsExplicit::No)
    }
}

impl<T: Into<Label>> From<(T, IsExplicit)> for IsaConstraint {
    fn from((label, is_explicit): (T, IsExplicit)) -> Self {
        IsaConstraint::new(TypeReference::Label(label.into()), is_explicit)
    }
}

impl From<ConceptVariable> for IsaConstraint {
    fn from(var: ConceptVariable) -> Self {
        IsaConstraint::new(TypeReference::Variable(var), IsExplicit::No)
    }
}

impl From<(ConceptVariable, IsExplicit)> for IsaConstraint {
    fn from((var, is_explicit): (ConceptVariable, IsExplicit)) -> Self {
        IsaConstraint::new(TypeReference::Variable(var), is_explicit)
    }
}

impl fmt::Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}",
            match self.is_explicit {
                IsExplicit::Yes => token::Constraint::IsaX,
                IsExplicit::No => token::Constraint::Isa,
            },
            self.type_reference
        )
    }
}
