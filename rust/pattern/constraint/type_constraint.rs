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

use crate::{Constraint, TypeVariable, TypeVariableBuilder, UnboundVariable};
use std::fmt;
use std::fmt::{Display, Formatter};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeConstraint {
    Label(LabelConstraint),
    Relates(RelatesConstraint),
    Plays(PlaysConstraint),
}

impl TypeConstraint {
    pub fn into_constraint(self) -> Constraint {
        Constraint::Type(self)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ScopedType {
    scope: Option<String>,
    name: String,
}

impl From<&str> for ScopedType {
    fn from(name: &str) -> Self {
        ScopedType::from(String::from(name))
    }
}

impl From<String> for ScopedType {
    fn from(name: String) -> Self {
        ScopedType { scope: None, name }
    }
}

impl From<(String, String)> for ScopedType {
    fn from((scope, name): (String, String)) -> Self {
        ScopedType {
            scope: Some(scope),
            name,
        }
    }
}

impl Display for ScopedType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(scope) = &self.scope {
            write!(f, "{}:", scope)?;
        }
        write!(f, "{}", self.name)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct LabelConstraint {
    pub scoped_type: ScopedType,
}

impl LabelConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_type_constraint().into_constraint()
    }

    pub fn into_type_constraint(self) -> TypeConstraint {
        TypeConstraint::Label(self)
    }
}

impl Display for LabelConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "type {}", self.scoped_type)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelatesConstraint {
    pub role_type: TypeVariable,
    pub overridden_role_type: Option<TypeVariable>,
}

impl RelatesConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_type_constraint().into_constraint()
    }

    pub fn into_type_constraint(self) -> TypeConstraint {
        TypeConstraint::Relates(self)
    }
}

impl From<&str> for RelatesConstraint {
    fn from(type_name: &str) -> Self {
        RelatesConstraint::from(String::from(type_name))
    }
}

impl From<String> for RelatesConstraint {
    fn from(type_name: String) -> Self {
        RelatesConstraint {
            role_type: UnboundVariable::hidden()
                .type_(ScopedType::from((String::from("relation"), type_name))),
            overridden_role_type: None,
        }
    }
}

impl From<UnboundVariable> for RelatesConstraint {
    fn from(role_type: UnboundVariable) -> Self {
        RelatesConstraint {
            role_type: role_type.into_type(),
            overridden_role_type: None,
        }
    }
}

impl Display for RelatesConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "relates {}", self.role_type)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct PlaysConstraint {
    pub role_type: TypeVariable,
    pub relation_type: Option<TypeVariable>,
    pub overridden_role_type: Option<TypeVariable>,
}

impl PlaysConstraint {
    pub fn into_type_constraint(self) -> TypeConstraint {
        TypeConstraint::Plays(self)
    }

    fn new(role_type: TypeVariable, overridden_role_type: Option<TypeVariable>) -> Self {
        PlaysConstraint {
            relation_type: match &role_type.label {
                Some(label) => Some(
                    UnboundVariable::hidden()
                        .type_(label.scoped_type.scope.as_ref().cloned().unwrap()),
                ),
                None => None,
            },
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
        PlaysConstraint::new(
            UnboundVariable::hidden().type_(ScopedType::from((relation_type, role_type))),
            None,
        )
    }
}

impl From<UnboundVariable> for PlaysConstraint {
    fn from(role_type: UnboundVariable) -> Self {
        PlaysConstraint::new(role_type.into_type(), None)
    }
}

impl Display for PlaysConstraint {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "plays {}", self.role_type)
    }
}