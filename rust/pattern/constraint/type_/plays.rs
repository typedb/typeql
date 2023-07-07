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
pub struct PlaysConstraint {
    pub role_type: TypeVariable,
    pub relation_type: Option<TypeVariable>,
    pub overridden_role_type: Option<TypeVariable>,
}

impl PlaysConstraint {
    pub(crate) fn new(role_type: TypeVariable, overridden_role_type: Option<TypeVariable>) -> Self {
        PlaysConstraint {
            relation_type: role_type
                .label
                .as_ref()
                .map(|label| UnboundConceptVariable::hidden().type_(label.label.scope.as_ref().cloned().unwrap())),
            role_type,
            overridden_role_type,
        }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(
            iter::once(&self.role_type.reference)
                .chain(self.relation_type.iter().map(|v| &v.reference))
                .chain(self.overridden_role_type.iter().map(|v| &v.reference)),
        )
    }
}

impl Validatable for PlaysConstraint {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut iter::once(self.role_type.validate())
                .chain(self.overridden_role_type.iter().map(Validatable::validate))
                .chain(self.relation_type.iter().map(Validatable::validate)),
        )
    }
}

impl From<(&str, &str)> for PlaysConstraint {
    fn from((relation_type, role_type): (&str, &str)) -> Self {
        PlaysConstraint::from((String::from(relation_type), String::from(role_type)))
    }
}

impl From<(&str, &str, &str)> for PlaysConstraint {
    fn from((relation_type, role_type, overridden_role_type): (&str, &str, &str)) -> Self {
        PlaysConstraint::from((
            String::from(relation_type),
            String::from(role_type),
            String::from(overridden_role_type),
        ))
    }
}

impl From<(String, String)> for PlaysConstraint {
    fn from((relation_type, role_type): (String, String)) -> Self {
        PlaysConstraint::from(Label::from((relation_type, role_type)))
    }
}

impl From<(String, String, String)> for PlaysConstraint {
    fn from((relation_type, role_type, overridden_role_type): (String, String, String)) -> Self {
        PlaysConstraint::from((Label::from((relation_type, role_type)), Label::from(overridden_role_type)))
    }
}

impl From<Label> for PlaysConstraint {
    fn from(role_type: Label) -> Self {
        PlaysConstraint::new(UnboundConceptVariable::hidden().type_(role_type), None)
    }
}

impl From<(Label, Label)> for PlaysConstraint {
    fn from((role_type, overridden_role_type): (Label, Label)) -> Self {
        PlaysConstraint::new(
            UnboundConceptVariable::hidden().type_(role_type),
            Some(UnboundConceptVariable::hidden().type_(overridden_role_type)),
        )
    }
}

impl From<UnboundConceptVariable> for PlaysConstraint {
    fn from(role_type: UnboundConceptVariable) -> Self {
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
        write!(f, "{} {}", token::Constraint::Plays, self.role_type)?;
        if let Some(overridden) = &self.overridden_role_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
