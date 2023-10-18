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
        statement::ValueConstrainable, AssignConstraint, LeftOperand, PredicateConstraint, Reference, ValueReference,
        ValueStatement,
    },
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub struct ValueVariable {
    pub reference: Reference,
}

impl ValueVariable {
    pub fn into_value_variable(self) -> ValueStatement {
        ValueStatement::new(self.reference)
    }

    pub fn named(name: String) -> ValueVariable {
        ValueVariable { reference: Reference::Value(ValueReference::Name(name)) }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.reference))
    }
}

impl ValueConstrainable for ValueVariable {
    fn constrain_assign(self, assign: AssignConstraint) -> ValueStatement {
        self.into_value_variable().constrain_assign(assign)
    }

    fn constrain_predicate(self, predicate: PredicateConstraint) -> ValueStatement {
        self.into_value_variable().constrain_predicate(predicate)
    }
}

impl Validatable for ValueVariable {
    fn validate(&self) -> Result {
        self.reference.validate()
    }
}

impl From<&str> for ValueVariable {
    fn from(name: &str) -> Self {
        ValueVariable::named(name.to_string())
    }
}

impl From<String> for ValueVariable {
    fn from(name: String) -> Self {
        ValueVariable::named(name)
    }
}

impl LeftOperand for ValueVariable {}

impl fmt::Display for ValueVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)
    }
}
