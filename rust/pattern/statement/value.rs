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
        AssignConstraint,
        Predicate, statement::builder::ValueConstrainable,
    },
};
use crate::variable::ValueVariable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueStatement {
    pub variable: ValueVariable,
    pub assign_constraint: Option<AssignConstraint>,
    pub predicate_constraint: Option<Predicate>,
}

impl ValueStatement {
    pub fn new(variable: ValueVariable) -> ValueStatement {
        ValueStatement { variable, assign_constraint: None, predicate_constraint: None }
    }

    pub fn owner(&self) -> VariableRef<'_> {
        VariableRef::Value(&self.variable)
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            iter::once(self.owner())
                .chain(self.assign_constraint.iter().flat_map(|assign| assign.variables()))
                .chain(self.predicate_constraint.iter().flat_map(|predicate| predicate.variables())),
        )
    }
}

impl Validatable for ValueStatement {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.variable.validate())
                .chain(self.assign_constraint.iter().map(Validatable::validate))
                .chain(self.predicate_constraint.iter().map(Validatable::validate)),
        )
    }
}

impl ValueConstrainable for ValueStatement {
    fn constrain_assign(self, assign: AssignConstraint) -> ValueStatement {
        Self { assign_constraint: Some(assign), ..self }
    }

    fn constrain_predicate(self, predicate: Predicate) -> ValueStatement {
        Self { predicate_constraint: Some(predicate), ..self }
    }
}

impl fmt::Display for ValueStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(assign) = &self.assign_constraint {
            write!(f, " {assign}")?;
        } else if let Some(predicate) = &self.predicate_constraint {
            write!(f, " {} {}", predicate.predicate, predicate.value)?;
        }
        Ok(())
    }
}
