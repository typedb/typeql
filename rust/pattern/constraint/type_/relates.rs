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
    common::token::Constraint::{As, Relates},
    Label, Type, TypeVariable, TypeVariableBuilder, UnboundVariable,
};
use std::fmt;

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

impl From<(&str, &str)> for RelatesConstraint {
    fn from((role_type, overridden_role_type): (&str, &str)) -> Self {
        RelatesConstraint {
            role_type: UnboundVariable::hidden().type_(role_type).unwrap(),
            overridden_role_type: Some(
                UnboundVariable::hidden().type_(overridden_role_type).unwrap(),
            ),
        }
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

impl From<(TypeVariable, Option<TypeVariable>)> for RelatesConstraint {
    fn from((role_type, overridden_role_type): (TypeVariable, Option<TypeVariable>)) -> Self {
        RelatesConstraint { role_type, overridden_role_type }
    }
}

impl From<Type> for RelatesConstraint {
    fn from(role_type: Type) -> Self {
        RelatesConstraint::from(role_type.into_type_variable())
    }
}

impl From<(Type, Option<Type>)> for RelatesConstraint {
    fn from((role_type, overridden): (Type, Option<Type>)) -> Self {
        RelatesConstraint::from((
            role_type.into_type_variable(),
            overridden.map(Type::into_type_variable),
        ))
    }
}

impl fmt::Display for RelatesConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Relates, self.role_type)?;
        if let Some(overridden) = &self.overridden_role_type {
            write!(f, " {} {}", As, overridden)?;
        }
        Ok(())
    }
}
