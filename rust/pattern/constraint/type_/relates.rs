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
    common::{error::collect_err, Result, token, validatable::Validatable},
    Label,
    variable::ConceptVariable,
};
use crate::variable::TypeReference;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelatesConstraint {
    pub role_type: TypeReference,
    pub overridden_role_type: Option<TypeReference>,
}

impl RelatesConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        Box::new(self.role_type.variables()
            .chain(self.overridden_role_type.iter().flat_map(|v| v.variables())))
    }
}

impl Validatable for RelatesConstraint {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.role_type.validate())
                .chain(self.overridden_role_type.iter().map(Validatable::validate)),
        )
    }
}

impl From<&str> for RelatesConstraint {
    fn from(role_name: &str) -> Self {
        RelatesConstraint::from(role_name.to_owned())
    }
}

impl From<String> for RelatesConstraint {
    fn from(role_name: String) -> Self {
        RelatesConstraint::from(Label::from(role_name))
    }
}

impl From<Label> for RelatesConstraint {
    fn from(role_name: Label) -> Self {
        RelatesConstraint::from(TypeReference::Label(role_name))
    }
}

impl From<ConceptVariable> for RelatesConstraint {
    fn from(role_type: ConceptVariable) -> Self {
        RelatesConstraint::from(TypeReference::Variable(role_type))
    }
}

impl From<TypeReference> for RelatesConstraint {
    fn from(role_type: TypeReference) -> Self {
        RelatesConstraint { role_type, overridden_role_type: None }
    }
}

impl From<(&str, &str)> for RelatesConstraint {
    fn from((role_name, overridden_role_name): (&str, &str)) -> Self {
        RelatesConstraint::from((role_name.to_owned(), overridden_role_name.to_owned()))
    }
}

impl From<(String, String)> for RelatesConstraint {
    fn from((role_name, overridden_role_name): (String, String)) -> Self {
        RelatesConstraint::from((Label::from(role_name), Label::from(overridden_role_name)))
    }
}

impl From<(Label, Label)> for RelatesConstraint {
    fn from((role_name, overridden_role_name): (Label, Label)) -> Self {
        RelatesConstraint::from((TypeReference::Label(role_name), TypeReference::Label(overridden_role_name)))
    }
}

impl From<(ConceptVariable, ConceptVariable)> for RelatesConstraint {
    fn from((role_type, overridden_role_name): (ConceptVariable, ConceptVariable)) -> Self {
        RelatesConstraint::from((TypeReference::Variable(role_type), TypeReference::Variable(overridden_role_name)))
    }
}

impl From<(TypeReference, TypeReference)> for RelatesConstraint {
    fn from((role_type, overridden_role_name): (TypeReference, TypeReference)) -> Self {
        RelatesConstraint { role_type, overridden_role_type: Some(overridden_role_name) }
    }
}

impl From<(TypeReference, Option<TypeReference>)> for RelatesConstraint {
    fn from((role_type, overridden_role_type): (TypeReference, Option<TypeReference>)) -> Self {
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
