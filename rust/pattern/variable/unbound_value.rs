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
    common::{validatable::Validatable, Result},
    pattern::{
        variable::ValueConstrainable, AssignConstraint, PredicateConstraint, Reference, ValueReference, ValueVariable,
    },
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub struct UnboundValueVariable {
    pub reference: Reference,
}

impl UnboundValueVariable {
    pub fn into_value_variable(self) -> ValueVariable {
        ValueVariable::new(self.reference)
    }

    pub fn named(name: String) -> UnboundValueVariable {
        UnboundValueVariable { reference: Reference::Value(ValueReference::Name(name)) }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.reference))
    }
}

impl ValueConstrainable for UnboundValueVariable {
    fn constrain_assign(self, assign: AssignConstraint) -> ValueVariable {
        self.into_value_variable().constrain_assign(assign)
    }

    fn constrain_predicate(self, predicate: PredicateConstraint) -> ValueVariable {
        self.into_value_variable().constrain_predicate(predicate)
    }
}

impl Validatable for UnboundValueVariable {
    fn validate(&self) -> Result<()> {
        self.reference.validate()
    }
}

impl From<&str> for UnboundValueVariable {
    fn from(name: &str) -> Self {
        UnboundValueVariable::named(name.to_string())
    }
}

impl From<String> for UnboundValueVariable {
    fn from(name: String) -> Self {
        UnboundValueVariable::named(name)
    }
}

impl fmt::Display for UnboundValueVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)
    }
}
