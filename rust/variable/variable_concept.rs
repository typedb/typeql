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
    pattern::LeftOperand,
    variable::variable::validate_variable_name,
};

#[derive(Debug, Clone, Copy, Eq, Hash, PartialEq)]
pub enum Visibility {
    Visible,
    Invisible,
}

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum ConceptVariable {
    Anonymous(Visibility),
    Name(String),
}

impl ConceptVariable {
    const ANONYMOUS_NAME: &'static str = token::Char::Underscore.as_str();

    pub fn named(name: String) -> ConceptVariable {
        ConceptVariable::Name(name)
    }

    pub fn anonymous() -> ConceptVariable {
        ConceptVariable::Anonymous(Visibility::Visible)
    }

    pub fn hidden() -> ConceptVariable {
        ConceptVariable::Anonymous(Visibility::Invisible)
    }

    pub fn is_visible(&self) -> bool {
        !matches!(self, Self::Anonymous(Visibility::Invisible))
    }

    pub fn is_name(&self) -> bool {
        matches!(self, Self::Name(_))
    }

    pub fn name(&self) -> &str {
        match self {
            Self::Anonymous(_) => Self::ANONYMOUS_NAME,
            Self::Name(name) => name,
        }
    }
}

impl Validatable for ConceptVariable {
    fn validate(&self) -> Result {
        match self {
            Self::Anonymous(_) => Ok(()),
            Self::Name(n) => validate_variable_name(n),
        }
    }
}

impl From<()> for ConceptVariable {
    fn from(_: ()) -> Self {
        ConceptVariable::anonymous()
    }
}

// TODO: these are ambiguous conversions (label vs named) - why do we need them?

impl From<&str> for ConceptVariable {
    fn from(name: &str) -> Self {
        ConceptVariable::named(name.to_string())
    }
}

impl From<String> for ConceptVariable {
    fn from(name: String) -> Self {
        ConceptVariable::named(name)
    }
}

impl LeftOperand for ConceptVariable {}

impl fmt::Display for ConceptVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", token::Char::Dollar, self.name())
    }
}
