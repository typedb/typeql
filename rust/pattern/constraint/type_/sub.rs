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

use std::{fmt, iter};

use crate::{
    common::{token, validatable::Validatable, Result},
    pattern::{variable::Reference, IsExplicit, TypeVariable, TypeVariableBuilder, UnboundConceptVariable},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SubConstraint {
    pub type_: Box<TypeVariable>,
    pub is_explicit: IsExplicit,
}

impl SubConstraint {
    fn new(type_: TypeVariable, is_explicit: IsExplicit) -> Self {
        Self { type_: Box::new(type_), is_explicit }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.type_.reference))
    }
}

impl Validatable for SubConstraint {
    fn validate(&self) -> Result<()> {
        self.type_.validate()
    }
}

impl<T: Into<Label>> From<T> for SubConstraint {
    fn from(scoped_type: T) -> Self {
        Self::from(UnboundConceptVariable::hidden().type_(scoped_type))
    }
}

impl From<UnboundConceptVariable> for SubConstraint {
    fn from(type_: UnboundConceptVariable) -> Self {
        Self::from(type_.into_type())
    }
}
impl From<TypeVariable> for SubConstraint {
    fn from(type_: TypeVariable) -> Self {
        Self::new(type_, IsExplicit::No)
    }
}

impl<T: Into<Label>> From<(T, IsExplicit)> for SubConstraint {
    fn from((scoped_type, is_explicit): (T, IsExplicit)) -> Self {
        Self::from((UnboundConceptVariable::hidden().type_(scoped_type), is_explicit))
    }
}

impl From<(UnboundConceptVariable, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (UnboundConceptVariable, IsExplicit)) -> Self {
        Self::from((type_.into_type(), is_explicit))
    }
}
impl From<(TypeVariable, IsExplicit)> for SubConstraint {
    fn from((type_, is_explicit): (TypeVariable, IsExplicit)) -> Self {
        Self::new(type_, is_explicit)
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
