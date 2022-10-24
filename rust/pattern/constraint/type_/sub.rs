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
    common::token::Constraint::Sub, Label, Type, TypeVariable, TypeVariableBuilder, UnboundVariable,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SubConstraint {
    pub type_: Box<TypeVariable>,
}

impl<T: Into<Label>> From<T> for SubConstraint {
    fn from(scoped_type: T) -> Self {
        SubConstraint { type_: Box::new(UnboundVariable::hidden().type_(scoped_type)) }
    }
}

impl From<UnboundVariable> for SubConstraint {
    fn from(type_: UnboundVariable) -> Self {
        Self::from(type_.into_type())
    }
}
impl From<TypeVariable> for SubConstraint {
    fn from(type_: TypeVariable) -> Self {
        SubConstraint { type_: Box::new(type_) }
    }
}

impl From<Type> for SubConstraint {
    fn from(type_: Type) -> Self {
        SubConstraint::from(type_.into_type_variable())
    }
}

impl fmt::Display for SubConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Sub, self.type_)
    }
}
