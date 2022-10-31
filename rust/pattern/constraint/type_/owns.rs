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
    pattern::{Type, TypeVariable, TypeVariableBuilder, UnboundVariable},
    Label,
};
use std::fmt;

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
            write!(f, " {}", token::Constraint::IsKey)?;
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
        OwnsConstraint::from((UnboundVariable::hidden().type_(attribute_type), is_key))
    }
}

impl From<(UnboundVariable, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (UnboundVariable, IsKeyAttribute)) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), is_key))
    }
}

impl From<TypeVariable> for OwnsConstraint {
    fn from(attribute_type: TypeVariable) -> Self {
        OwnsConstraint::new(attribute_type, None, IsKeyAttribute::No)
    }
}

impl From<(TypeVariable, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (TypeVariable, IsKeyAttribute)) -> Self {
        OwnsConstraint::new(attribute_type, None, is_key)
    }
}

impl From<Type> for OwnsConstraint {
    fn from(attribute_type: Type) -> Self {
        OwnsConstraint::from(attribute_type.into_type_variable())
    }
}

impl From<(Type, IsKeyAttribute)> for OwnsConstraint {
    fn from((attribute_type, is_key): (Type, IsKeyAttribute)) -> Self {
        OwnsConstraint::new(attribute_type.into_type_variable(), None, is_key)
    }
}

impl From<(&str, &str)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (&str, &str)) -> Self {
        OwnsConstraint::from((attribute_type, overridden_attribute_type, IsKeyAttribute::No))
    }
}

impl From<(&str, &str, IsKeyAttribute)> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, is_key): (&str, &str, IsKeyAttribute),
    ) -> Self {
        OwnsConstraint::from((
            Label::from(attribute_type),
            Label::from(overridden_attribute_type),
            is_key,
        ))
    }
}

impl From<(Label, Label, IsKeyAttribute)> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, is_key): (Label, Label, IsKeyAttribute),
    ) -> Self {
        OwnsConstraint::new(
            UnboundVariable::hidden().type_(attribute_type),
            Some(UnboundVariable::hidden().type_(overridden_attribute_type)),
            is_key,
        )
    }
}

impl From<(Type, Option<Type>)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (Type, Option<Type>)) -> Self {
        OwnsConstraint::new(
            attribute_type.into_type_variable(),
            overridden_attribute_type.map(Type::into_type_variable),
            IsKeyAttribute::No,
        )
    }
}

impl From<(Type, Option<Type>, IsKeyAttribute)> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, is_key): (Type, Option<Type>, IsKeyAttribute),
    ) -> Self {
        OwnsConstraint::new(
            attribute_type.into_type_variable(),
            overridden_attribute_type.map(Type::into_type_variable),
            is_key,
        )
    }
}

impl fmt::Display for OwnsConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}{}", token::Constraint::Owns, self.attribute_type, self.is_key)?;
        if let Some(overridden) = &self.overridden_attribute_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
