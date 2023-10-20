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
    common::{error::collect_err, Result, validatable::Validatable},
    pattern::{
        constraint::IsConstraint,
        statement::builder::ConceptConstrainable,
    },
};
use crate::variable::ConceptVariable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ConceptStatement {
    pub variable: ConceptVariable,
    pub is_constraint: Option<IsConstraint>,
}

impl ConceptStatement {
    pub fn new(variable: ConceptVariable) -> ConceptStatement {
        ConceptStatement { variable, is_constraint: None }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(iter::once(VariableRef::Concept(&self.variable))
            .chain(self.is_constraint.iter().map(|is| VariableRef::Concept(&is.variable.variable))))
    }
}

impl Validatable for ConceptStatement {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.variable.validate())
                .chain(self.is_constraint.iter().map(Validatable::validate)),
        )
    }
}

impl ConceptConstrainable for ConceptStatement {
    fn constrain_is(self, is: IsConstraint) -> ConceptStatement {
        Self { is_constraint: Some(is), ..self }
    }
}

impl fmt::Display for ConceptStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(is) = &self.is_constraint {
            write!(f, " {is}")?;
        }
        Ok(())
    }
}
