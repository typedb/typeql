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

use crate::common::string::escape_regex;
use crate::common::token::Constraint::*;
use crate::{TypeVariable, TypeVariableBuilder, UnboundVariable};
use std::fmt;

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

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct LabelConstraint {
    pub label: Label,
}

impl fmt::Display for LabelConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Type, self.label)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SubConstraint {
    pub type_: Box<TypeVariable>,
}

impl<T: Into<Label>> From<T> for SubConstraint {
    fn from(scoped_type: T) -> Self {
        SubConstraint { type_: Box::new(UnboundVariable::hidden().type_(scoped_type).unwrap()) }
    }
}

impl From<UnboundVariable> for SubConstraint {
    fn from(type_: UnboundVariable) -> Self {
        Self::from(type_.into_type())
    }
}
impl From<TypeVariable> for SubConstraint {
    fn from(type_: TypeVariable) -> Self {
        SubConstraint { type_: Box::new(type_) }
    }
}

impl fmt::Display for SubConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Sub, self.type_)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelatesConstraint {
    pub role_type: TypeVariable,
    pub overridden_role_type: Option<TypeVariable>,
}

impl From<&str> for RelatesConstraint {
    fn from(type_name: &str) -> Self {
        RelatesConstraint::from(String::from(type_name))
    }
}

impl From<String> for RelatesConstraint {
    fn from(type_name: String) -> Self {
        RelatesConstraint::from(Label::from(type_name))
    }
}

impl From<Label> for RelatesConstraint {
    fn from(type_: Label) -> Self {
        RelatesConstraint {
            role_type: UnboundVariable::hidden().type_(type_).unwrap(),
            overridden_role_type: None,
        }
    }
}

impl From<UnboundVariable> for RelatesConstraint {
    fn from(role_type: UnboundVariable) -> Self {
        RelatesConstraint::from(role_type.into_type())
    }
}

impl From<TypeVariable> for RelatesConstraint {
    fn from(role_type: TypeVariable) -> Self {
        RelatesConstraint { role_type, overridden_role_type: None }
    }
}

impl fmt::Display for RelatesConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Relates, self.role_type)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct PlaysConstraint {
    pub role_type: TypeVariable,
    pub relation_type: Option<TypeVariable>,
    pub overridden_role_type: Option<TypeVariable>,
}

impl PlaysConstraint {
    fn new(role_type: TypeVariable, overridden_role_type: Option<TypeVariable>) -> Self {
        PlaysConstraint {
            relation_type: role_type.label.as_ref().map(|label| {
                UnboundVariable::hidden()
                    .type_(label.label.scope.as_ref().cloned().unwrap())
                    .unwrap()
            }),
            role_type,
            overridden_role_type,
        }
    }
}

impl From<(&str, &str)> for PlaysConstraint {
    fn from((relation_type, role_type): (&str, &str)) -> Self {
        PlaysConstraint::from((String::from(relation_type), String::from(role_type)))
    }
}

impl From<(String, String)> for PlaysConstraint {
    fn from((relation_type, role_type): (String, String)) -> Self {
        PlaysConstraint::from(Label::from((relation_type, role_type)))
    }
}

impl From<Label> for PlaysConstraint {
    fn from(scoped_type: Label) -> Self {
        PlaysConstraint::new(UnboundVariable::hidden().type_(scoped_type).unwrap(), None)
    }
}

impl From<UnboundVariable> for PlaysConstraint {
    fn from(role_type: UnboundVariable) -> Self {
        PlaysConstraint::from(role_type.into_type())
    }
}

impl From<TypeVariable> for PlaysConstraint {
    fn from(role_type: TypeVariable) -> Self {
        PlaysConstraint::new(role_type, None)
    }
}

impl fmt::Display for PlaysConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Plays, self.role_type)
    }
}

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum IsKeyAttribute {
    Yes,
    No,
}

impl From<bool> for IsKeyAttribute {
    fn from(is_key: bool) -> Self {
        match is_key {
            true => IsKeyAttribute::Yes,
            false => IsKeyAttribute::No,
        }
    }
}

pub const KEY: IsKeyAttribute = IsKeyAttribute::Yes;

impl fmt::Display for IsKeyAttribute {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if *self == IsKeyAttribute::Yes {
            write!(f, " {}", IsKey)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct OwnsConstraint {
    pub attribute_type: TypeVariable,
    pub overridden_attribute_type: Option<TypeVariable>,
    pub is_key: IsKeyAttribute,
}

impl OwnsConstraint {
    fn new(
        attribute_type: TypeVariable,
        overridden_attribute_type: Option<TypeVariable>,
        is_key: IsKeyAttribute,
    ) -> Self {
        OwnsConstraint { attribute_type, overridden_attribute_type, is_key }
    }
}

impl From<&str> for OwnsConstraint {
    fn from(attribute_type: &str) -> Self {
        OwnsConstraint::from((attribute_type, IsKeyAttribute::No))
    }
}

impl From<(&str, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (&str, IsKeyAttribute)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), is_key))
    }
}

impl From<(String, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (String, IsKeyAttribute)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), is_key))
    }
}

impl From<(Label, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (Label, IsKeyAttribute)) -> Self {
        OwnsConstraint::from((UnboundVariable::hidden().type_(attribute_type).unwrap(), is_key))
    }
}

impl From<(UnboundVariable, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (UnboundVariable, IsKeyAttribute)) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), is_key))
    }
}

impl From<(TypeVariable, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (TypeVariable, IsKeyAttribute)) -> Self {
        OwnsConstraint::new(attribute_type, None, is_key)
    }
}

impl fmt::Display for OwnsConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}{}", Owns, self.attribute_type, self.is_key)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RegexConstraint {
    regex: String,
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
        write!(f, r#"{} "{}""#, Regex, escape_regex(&self.regex))
    }
}
