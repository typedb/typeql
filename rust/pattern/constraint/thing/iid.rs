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

use crate::common::{error::TypeQLError, token, validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IIDConstraint {
    pub iid: String,
}

fn is_valid_iid(iid: &str) -> bool {
    iid.starts_with("0x") && iid.chars().skip(2).all(|c| c.is_ascii_hexdigit() && !c.is_uppercase())
}

impl IIDConstraint {
    pub fn new(iid: String) -> Self {
        IIDConstraint { iid }
    }
}

impl Validatable for IIDConstraint {
    fn validate(&self) -> Result<()> {
        if !is_valid_iid(&self.iid) {
            Err(TypeQLError::InvalidIIDString(self.iid.clone()))?
        }
        Ok(())
    }
}

impl From<&str> for IIDConstraint {
    fn from(iid: &str) -> Self {
        IIDConstraint::new(iid.to_string())
    }
}

impl From<String> for IIDConstraint {
    fn from(iid: String) -> Self {
        IIDConstraint::new(iid)
    }
}

impl fmt::Display for IIDConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::IID, self.iid)
    }
}
