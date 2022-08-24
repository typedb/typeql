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
use std::fmt::Display;
use crate::{Constraint, enum_getter, Variable};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ThingConstraint {
    Isa(IsaConstraint),
    Has(HasConstraint),
    Value(ValueConstraint),
}

impl ThingConstraint {
    enum_getter!(into_isa, Isa, IsaConstraint);
    enum_getter!(into_has, Has, HasConstraint);
    enum_getter!(into_value, Value, ValueConstraint);

    pub fn into_constraint(self) -> Constraint {
        Constraint::Thing(self)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_name: String,
    pub is_explicit: bool,
}

impl IsaConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_thing_constraint().into_constraint()
    }

    pub fn into_thing_constraint(self) -> ThingConstraint {
        ThingConstraint::Isa(self)
    }
}

impl Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "isa {}", self.type_name)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Value {
    String(String),
    Variable(Variable),
}

impl From<String> for Value {
    fn from(string: String) -> Value {
        Value::String(string)
    }
}

impl From<Variable> for Value {
    fn from(variable: Variable) -> Value {
        Value::Variable(variable)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct HasConstraint {
    pub type_name: String,
    pub value: Value,
}

impl HasConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_thing_constraint().into_constraint()
    }

    pub fn into_thing_constraint(self) -> ThingConstraint {
        ThingConstraint::Has(self)
    }

    pub fn new<T: Into<Value>>(type_name: String, value: T) -> Self {
        HasConstraint { type_name, value: value.into() }
    }
}

impl Display for HasConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "has {}", self.type_name)
    }
}
