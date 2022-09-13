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

use crate::enum_getter;
use crate::pattern::*;
use std::fmt;
use std::fmt::Display;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Bound(BoundVariable),
    Unbound(UnboundVariable),
}

impl Variable {
    pub fn into_pattern(self) -> Pattern {
        self.into_conjunctable().into_pattern()
    }

    pub fn into_conjunctable(self) -> Conjunctable {
        Conjunctable::Variable(self)
    }

    pub fn into_type(self) -> TypeVariable {
        use Variable::*;
        match self {
            Bound(var) => var.into_type(),
            Unbound(var) => var.into_type(),
        }
    }

    pub fn into_thing(self) -> ThingVariable {
        use Variable::*;
        match self {
            Bound(var) => var.into_thing(),
            Unbound(var) => var.into_thing(),
        }
    }
}

impl From<UnboundVariable> for Variable {
    fn from(unbound: UnboundVariable) -> Self {
        Variable::Unbound(unbound)
    }
}

impl<T> From<T> for Variable
where
    BoundVariable: From<T>,
{
    fn from(var: T) -> Self {
        Variable::Bound(BoundVariable::from(var))
    }
}

impl Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Variable::*;
        match self {
            Unbound(unbound) => write!(f, "{}", unbound),
            Bound(bound) => write!(f, "{}", bound),
        }
    }
}
