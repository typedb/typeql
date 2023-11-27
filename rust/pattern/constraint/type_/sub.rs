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
    common::{token, validatable::Validatable, Result},
    pattern::IsExplicit,
    variable::{variable::VariableRef, ConceptVariable, TypeReference},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SubConstraint {
    pub type_: TypeReference,
    pub is_explicit: IsExplicit,
}

impl SubConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.type_.variables()
    }
}

impl Validatable for SubConstraint {
    fn validate(&self) -> Result {
        self.type_.validate()
    }
}

impl<T: Into<Label>> From<T> for SubConstraint {
    fn from(type_: T) -> Self {
        Self::from(TypeReference::Label(type_.into()))
    }
}

impl From<ConceptVariable> for SubConstraint {
    fn from(type_: ConceptVariable) -> Self {
        Self::from(TypeReference::Variable(type_))
    }
}

impl From<TypeReference> for SubConstraint {
    fn from(type_: TypeReference) -> Self {
        SubConstraint { type_, is_explicit: IsExplicit::No }
    }
}

impl<T: Into<Label>> From<(T, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (T, IsExplicit)) -> Self {
        Self::from((TypeReference::Label(type_.into()), is_explicit))
    }
}

impl From<(ConceptVariable, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (ConceptVariable, IsExplicit)) -> Self {
        Self::from((TypeReference::Variable(type_), is_explicit))
    }
}

impl From<(TypeReference, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (TypeReference, IsExplicit)) -> Self {
        Self { type_, is_explicit }
    }
}

impl fmt::Display for SubConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{} {}",
            match self.is_explicit {
                IsExplicit::Yes => token::Constraint::SubX,
                IsExplicit::No => token::Constraint::Sub,
            },
            self.type_
        )
    }
}
