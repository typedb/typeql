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

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    pattern::{variable::Reference, TypeVariable, TypeVariableBuilder, UnboundConceptVariable},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelatesConstraint {
    pub role_type: TypeVariable,
    pub overridden_role_type: Option<TypeVariable>,
}

impl RelatesConstraint {
    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.role_type.reference).chain(self.overridden_role_type.iter().map(|v| &v.reference)))
    }
}

impl Validatable for RelatesConstraint {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut iter::once(self.role_type.validate())
                .chain(self.overridden_role_type.iter().map(Validatable::validate)),
        )
    }
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
            role_type: UnboundConceptVariable::hidden().type_(role_type),
            overridden_role_type: Some(UnboundConceptVariable::hidden().type_(overridden_role_type)),
        }
    }
}

impl From<Label> for RelatesConstraint {
    fn from(type_: Label) -> Self {
        RelatesConstraint { role_type: UnboundConceptVariable::hidden().type_(type_), overridden_role_type: None }
    }
}

impl From<UnboundConceptVariable> for RelatesConstraint {
    fn from(role_type: UnboundConceptVariable) -> Self {
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

impl fmt::Display for RelatesConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Relates, self.role_type)?;
        if let Some(overridden) = &self.overridden_role_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
