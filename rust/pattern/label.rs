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

use crate::common::{error::TypeQLError, identifier::is_valid_identifier, token, validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Label {
    pub scope: Option<String>,
    pub name: String,
}

impl From<token::Type> for Label {
    fn from(name: token::Type) -> Self {
        Label::from(name.to_string())
    }
}

impl From<&str> for Label {
    fn from(name: &str) -> Self {
        Label::from(String::from(name))
    }
}

impl From<String> for Label {
    fn from(name: String) -> Self {
        Label { scope: None, name }
    }
}

impl From<(&str, &str)> for Label {
    fn from((scope, name): (&str, &str)) -> Self {
        Label::from((scope.to_string(), name.to_string()))
    }
}

impl From<(String, String)> for Label {
    fn from((scope, name): (String, String)) -> Self {
        Label { scope: Some(scope), name }
    }
}

impl Validatable for Label {
    fn validate(&self) -> Result {
        validate_label(&self.name)?;
        if let Some(scope_name) = &self.scope {
            validate_label(scope_name)?
        }
        Ok(())
    }
}

fn validate_label(label: &str) -> Result {
    if !is_valid_identifier(label) {
        Err(TypeQLError::InvalidTypeLabel { label: label.to_owned() })?
    } else {
        Ok(())
    }
}

impl fmt::Display for Label {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(scope) = &self.scope {
            write!(f, "{scope}:")?;
        }
        write!(f, "{}", self.name)
    }
}
