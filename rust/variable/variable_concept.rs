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
    pattern::LeftOperand,
    variable::variable::validate_variable_name,
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum ConceptVariable {
    Anonymous,
    Hidden,
    Named(String),
}

impl ConceptVariable {
    const ANONYMOUS_NAME: &'static str = token::Char::Underscore.as_str();

    pub fn is_visible(&self) -> bool {
        self != &Self::Hidden
    }

    pub fn is_named(&self) -> bool {
        matches!(self, Self::Named(_))
    }

    pub fn name(&self) -> &str {
        match self {
            Self::Anonymous | Self::Hidden => Self::ANONYMOUS_NAME,
            Self::Named(name) => name,
        }
    }
}

impl Validatable for ConceptVariable {
    fn validate(&self) -> Result {
        match self {
            Self::Anonymous | Self::Hidden => Ok(()),
            Self::Named(n) => validate_variable_name(n),
        }
    }
}

impl From<()> for ConceptVariable {
    fn from(_: ()) -> Self {
        ConceptVariable::Anonymous
    }
}

impl From<&str> for ConceptVariable {
    fn from(name: &str) -> Self {
        ConceptVariable::Named(name.to_string())
    }
}

impl From<String> for ConceptVariable {
    fn from(name: String) -> Self {
        ConceptVariable::Named(name)
    }
}

impl LeftOperand for ConceptVariable {}

impl fmt::Display for ConceptVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", token::Char::Dollar, self.name())
    }
}
