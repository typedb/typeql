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
use std::hash::{Hash, Hasher};

use crate::{
    common::{error::collect_err, validatable::Validatable, Result},
    pattern::{
        constraint::IsConstraint,
        variable::{builder::ValueConstrainable, Reference},
    },
};
use crate::common::token;
use crate::pattern::{AssignConstraint, ConceptVariable, Predicate};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueVariable {
    pub reference: Reference,
    pub assign_constraint: Option<AssignConstraint>,
    pub predicate_constraint: Option<Predicate>,
}

impl ValueVariable {
    pub fn new(reference: Reference) -> ValueVariable {
        ValueVariable { reference, assign_constraint: None, predicate_constraint: None }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        // FIXME
        Box::new(iter::once(&self.reference)
            .chain(self.assign_constraint.unwrap().variables().iter().map(|var| var.references())))
    }
}

impl Validatable for ValueVariable {
    fn validate(&self) -> Result<()> {
        todo!();
        // collect_err(
        //     &mut iter::once(self.reference.validate()).chain(self.is_constraint.iter().map(Validatable::validate)),
        // )
    }
}

impl ValueConstrainable for ValueVariable {
    fn constrain_assign(self, assign: AssignConstraint) -> ValueVariable {
        Self { assign_constraint: Some(assign), ..self }
    }

    fn constrain_predicate(self, predicate: Predicate) -> ValueVariable {
        Self { predicate_constraint: Some(predicate), ..self }
    }
}

impl fmt::Display for ValueVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)?;
        if let Some(assign) = &self.assign_constraint {
            write!(f, " {} {}", token::Constraint::Assign, assign)?;
        } else if let Some(predicate) = &self.predicate_constraint {
            write!(f, " {} {}", predicate.predicate, predicate.value)?;
        }
        Ok(())
    }
}

impl Hash for ValueVariable {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.reference.hash(state);
    }
}