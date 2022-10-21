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
    common::token::Constraint::Plays, Label, Type, TypeVariable, TypeVariableBuilder,
    UnboundVariable,
};
use std::fmt;

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
                UnboundVariable::hidden().type_(label.label.scope.as_ref().cloned().unwrap())
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
        PlaysConstraint::new(UnboundVariable::hidden().type_(scoped_type), None)
    }
}

impl From<UnboundVariable> for PlaysConstraint {
    fn from(role_type: UnboundVariable) -> Self {
        PlaysConstraint::from(role_type.into_type())
    }
}

impl From<Type> for PlaysConstraint {
    fn from(role_type: Type) -> Self {
        PlaysConstraint::new(role_type.into_type_variable(), None)
    }
}

impl From<(Type, Option<Type>)> for PlaysConstraint {
    fn from((role_type, overridden_role_type): (Type, Option<Type>)) -> Self {
        PlaysConstraint::new(
            role_type.into_type_variable(),
            overridden_role_type.map(Type::into_type_variable),
        )
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
