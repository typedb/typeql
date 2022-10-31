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
    common::token,
    pattern::{
        variable::{TypeVariable, UnboundVariable},
        TypeVariableBuilder,
    },
};
use std::fmt;

#[derive(Debug)]
pub enum Type {
    Label(Label),
    Variable(UnboundVariable),
}

impl Type {
    pub fn into_type_variable(self) -> TypeVariable {
        match self {
            Self::Label(label) => UnboundVariable::hidden().type_(label),
            Self::Variable(var) => var.into_type(),
        }
    }
}

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

impl fmt::Display for Label {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(scope) = &self.scope {
            write!(f, "{}:", scope)?;
        }
        write!(f, "{}", self.name)
    }
}
