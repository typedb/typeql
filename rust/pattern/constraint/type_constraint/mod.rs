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

use crate::{TypeVariable, TypeVariableBuilder, UnboundVariable};
use std::fmt;

pub mod label_constraint;
pub use label_constraint::*;

pub mod owns_constraint;
pub use owns_constraint::*;

pub mod plays_constraint;
pub use plays_constraint::*;

pub mod regex_constraint;
pub use regex_constraint::*;

pub mod relates_constraint;
pub use relates_constraint::*;

pub mod sub_constraint;
pub use sub_constraint::*;

#[derive(Debug)]
pub enum Type {
    Label(Label),
    Variable(TypeVariable),
}

impl Type {
    pub fn into_variable(self) -> TypeVariable {
        match self {
            Self::Label(label) => UnboundVariable::hidden().type_(label).unwrap(),
            Self::Variable(var) => var,
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Label {
    scope: Option<String>,
    name: String,
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
