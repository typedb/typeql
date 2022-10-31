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

use crate::pattern::{
    constraint::IsConstraint,
    variable::{builder::ConceptConstrainable, Reference},
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ConceptVariable {
    pub reference: Reference,
    pub is_constraint: Option<IsConstraint>,
}

impl ConceptVariable {
    pub fn new(reference: Reference) -> ConceptVariable {
        ConceptVariable { reference, is_constraint: None }
    }
}

impl ConceptConstrainable for ConceptVariable {
    fn constrain_is(self, is: IsConstraint) -> ConceptVariable {
        Self { is_constraint: Some(is), ..self }
    }
}

impl fmt::Display for ConceptVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)?;
        if let Some(is) = &self.is_constraint {
            write!(f, " {}", is)?;
        }
        Ok(())
    }
}
