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
    pattern::{IsExplicit, Reference, TypeVariable, TypeVariableBuilder, UnboundConceptVariable},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_: TypeVariable,
    pub is_explicit: IsExplicit,
}

impl IsaConstraint {
    fn new(type_: TypeVariable, is_explicit: IsExplicit) -> Self {
        IsaConstraint { type_, is_explicit }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.type_.reference))
    }

    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        self.references()
    }
}

impl Validatable for IsaConstraint {
    fn validate(&self) -> Result<()> {
        self.type_.validate()
    }
}

impl<T: Into<Label>> From<T> for IsaConstraint {
    fn from(type_name: T) -> Self {
        IsaConstraint::new(UnboundConceptVariable::hidden().type_(type_name), IsExplicit::No)
    }
}

impl<T: Into<Label>> From<(T, IsExplicit)> for IsaConstraint {
    fn from((type_name, is_explicit): (T, IsExplicit)) -> Self {
        IsaConstraint::new(UnboundConceptVariable::hidden().type_(type_name), is_explicit)
    }
}

impl From<UnboundConceptVariable> for IsaConstraint {
    fn from(var: UnboundConceptVariable) -> Self {
        IsaConstraint::new(var.into_type(), IsExplicit::No)
    }
}

impl From<(UnboundConceptVariable, IsExplicit)> for IsaConstraint {
    fn from((var, is_explicit): (UnboundConceptVariable, IsExplicit)) -> Self {
        IsaConstraint::new(var.into_type(), is_explicit)
    }
}

impl fmt::Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{} {}",
            match self.is_explicit {
                IsExplicit::Yes => token::Constraint::IsaX,
                IsExplicit::No => token::Constraint::Isa,
            },
            self.type_
        )
    }
}
