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

use crate::common::{error::TypeQLError, validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum ValueReference {
    Name(String),
}

impl ValueReference {
    pub fn is_name(&self) -> bool {
        true
    }

    pub fn is_visible(&self) -> bool {
        true
    }
}

impl Validatable for ValueReference {
    fn validate(&self) -> Result<()> {
        match self {
            Self::Name(n) => expect_valid_identifier(n),
        }
    }
}

fn expect_valid_identifier(name: &str) -> Result<()> {
    if !name.starts_with(|c: char| c.is_ascii_alphanumeric())
        || !name.chars().all(|c| c.is_ascii_alphanumeric() || c == '_' || c == '-')
    {
        Err(TypeQLError::InvalidVariableName(name.to_string()))?
    }
    Ok(())
}

impl fmt::Display for ValueReference {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use ValueReference::*;
        write!(
            f,
            "?{}",
            match self {
                Name(name) => name,
            }
        )
    }
}
