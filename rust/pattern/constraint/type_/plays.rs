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
    variable::{variable::VariableRef, ConceptVariable, TypeReference},
    Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct PlaysConstraint {
    pub role_type: TypeReference,
    pub overridden_role_type: Option<TypeReference>,
}

impl PlaysConstraint {
    pub(crate) fn new(role_type: TypeReference, overridden_role_type: Option<TypeReference>) -> Self {
        PlaysConstraint { role_type, overridden_role_type }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.role_type.variables().chain(self.overridden_role_type.iter().flat_map(|t| t.variables())))
    }
}

impl Validatable for PlaysConstraint {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.role_type.validate()).chain(self.overridden_role_type.iter().map(Validatable::validate)),
        )
    }
}

impl From<Label> for PlaysConstraint {
    fn from(role_type: Label) -> Self {
        PlaysConstraint::from(TypeReference::Label(role_type))
    }
}

impl From<ConceptVariable> for PlaysConstraint {
    fn from(role_type: ConceptVariable) -> Self {
        PlaysConstraint::from(TypeReference::Variable(role_type))
    }
}

impl From<TypeReference> for PlaysConstraint {
    fn from(role_type: TypeReference) -> Self {
        PlaysConstraint::new(role_type, None)
    }
}

impl From<(&str, &str)> for PlaysConstraint {
    fn from((relation_type, role_name): (&str, &str)) -> Self {
        PlaysConstraint::from((relation_type.to_owned(), role_name.to_owned()))
    }
}

impl From<(String, String)> for PlaysConstraint {
    fn from((relation_type, role_name): (String, String)) -> Self {
        PlaysConstraint::from(Label::from((relation_type, role_name)))
    }
}

impl From<(Label, Label)> for PlaysConstraint {
    fn from((relation_type, role_name): (Label, Label)) -> Self {
        PlaysConstraint::from((TypeReference::Label(relation_type), TypeReference::Label(role_name)))
    }
}

impl From<(ConceptVariable, ConceptVariable)> for PlaysConstraint {
    fn from((role_type, overridden_role_type): (ConceptVariable, ConceptVariable)) -> Self {
        PlaysConstraint::from((TypeReference::Variable(role_type), TypeReference::Variable(overridden_role_type)))
    }
}

impl<T, U, V> From<(T, U, V)> for PlaysConstraint
where
    (T, U): Into<Label>,
    V: Into<Label>,
{
    fn from((relation_type, role_type, overridden_role_name): (T, U, V)) -> Self {
        PlaysConstraint::from(((relation_type, role_type).into(), overridden_role_name.into()))
    }
}

impl From<(TypeReference, TypeReference)> for PlaysConstraint {
    fn from((role_type, overridden_role_type): (TypeReference, TypeReference)) -> Self {
        PlaysConstraint::new(role_type, Some(overridden_role_type))
    }
}

impl fmt::Display for PlaysConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Plays, self.role_type)?;
        if let Some(overridden) = &self.overridden_role_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
