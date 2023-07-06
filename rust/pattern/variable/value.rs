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
    common::{error::collect_err, validatable::Validatable, Result},
    pattern::{
        constraint::IsConstraint,
        variable::{builder::ValueConstrainable, Reference},
    },
};
use crate::pattern::ConceptVariable;

//FIXME: It's still just a copy of ConceptVariable
#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueVariable {
    pub reference: Reference,
    pub is_constraint: Option<IsConstraint>,
}

impl ValueVariable {
    pub fn new(reference: Reference) -> ValueVariable {
        ValueVariable { reference, is_constraint: None }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.reference).chain(self.is_constraint.iter().map(|is| &is.variable.reference)))
    }
}

impl Validatable for ValueVariable {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut iter::once(self.reference.validate()).chain(self.is_constraint.iter().map(Validatable::validate)),
        )
    }
}

impl ValueConstrainable for ValueVariable {
    fn constrain_is(self, is: IsConstraint) -> ValueVariable {
        Self { is_constraint: Some(is), ..self }
    }
}

impl fmt::Display for ValueVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)?;
        if let Some(is) = &self.is_constraint {
            write!(f, " {is}")?;
        }
        Ok(())
    }
}
