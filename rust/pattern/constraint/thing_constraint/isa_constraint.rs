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

use crate::{
    common::token::Constraint::Isa, Label, TypeVariable, TypeVariableBuilder, UnboundVariable,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_: TypeVariable,
    pub is_explicit: bool,
}

impl<T: Into<Label>> From<T> for IsaConstraint {
    fn from(type_name: T) -> Self {
        IsaConstraint {
            type_: UnboundVariable::hidden().type_(type_name).unwrap(),
            is_explicit: false,
        }
    }
}

impl From<UnboundVariable> for IsaConstraint {
    fn from(var: UnboundVariable) -> Self {
        IsaConstraint::from(var.into_type())
    }
}

impl From<TypeVariable> for IsaConstraint {
    fn from(type_: TypeVariable) -> Self {
        IsaConstraint { type_, is_explicit: false }
    }
}

impl fmt::Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Isa, self.type_)
    }
}
