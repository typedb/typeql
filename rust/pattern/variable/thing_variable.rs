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

use crate::pattern::*;
use std::fmt;
use std::fmt::Display;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ThingVariable {
    pub reference: Reference,
    pub isa: Option<IsaConstraint>,
    pub has: Vec<HasConstraint>,
}

impl ThingVariable {
    pub fn into_pattern(self) -> Pattern {
        self.into_bound_variable().into_pattern()
    }

    pub fn into_bound_variable(self) -> BoundVariable {
        BoundVariable::Thing(self)
    }

    pub fn new(reference: Reference) -> ThingVariable {
        ThingVariable {
            reference,
            isa: None,
            has: Vec::new(),
        }
    }
}

impl ThingVariableBuilderCommon for ThingVariable {
    fn constrain_thing(mut self, constraint: ThingConstraint) -> ThingVariable {
        use ThingConstraint::*;
        match constraint {
            Isa(isa) => self.isa = Some(isa),
            Has(has) => self.has.push(has),
        }
        self
    }
}


impl Display for ThingVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let mut var = self.reference.syntax();
        if let Some(isa) = &self.isa {
            var.push(' ');
            var += &isa.to_string();
        }
        write!(f, "{}", var)
    }
}
