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
    pattern::{LeftOperand, ValueStatement},
    variable::variable::validate_variable_name,
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum ValueVariable {
    Named(String),
}

impl ValueVariable {
    pub fn into_value(self) -> ValueStatement {
        ValueStatement::new(self)
    }

    pub fn is_named(&self) -> bool {
        true
    }

    pub fn is_visible(&self) -> bool {
        true
    }

    pub fn name(&self) -> &str {
        let Self::Named(name) = self;
        name
    }
}

impl Validatable for ValueVariable {
    fn validate(&self) -> Result {
        match self {
            Self::Named(n) => validate_variable_name(n),
        }
    }
}

impl From<&str> for ValueVariable {
    fn from(name: &str) -> Self {
        ValueVariable::Named(name.to_owned())
    }
}

impl From<String> for ValueVariable {
    fn from(name: String) -> Self {
        ValueVariable::Named(name)
    }
}

impl LeftOperand for ValueVariable {}

impl fmt::Display for ValueVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", token::Char::Question, self.name())
    }
}
