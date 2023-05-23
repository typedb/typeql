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

use regex::Regex;

use crate::common::{error::TypeQLError, string::escape_regex, token, validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RegexConstraint {
    regex: String,
}

impl Validatable for RegexConstraint {
    fn validate(&self) -> Result<()> {
        if Regex::new(&self.regex).is_err() {
            Err(TypeQLError::InvalidAttributeTypeRegex(self.regex.clone()))?;
        }
        Ok(())
    }
}

impl From<&str> for RegexConstraint {
    fn from(regex: &str) -> Self {
        RegexConstraint { regex: regex.to_string() }
    }
}

impl From<String> for RegexConstraint {
    fn from(regex: String) -> Self {
        RegexConstraint { regex }
    }
}

impl fmt::Display for RegexConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, r#"{} "{}""#, token::Constraint::Regex, escape_regex(&self.regex))
    }
}
