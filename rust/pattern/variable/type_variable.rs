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

use std::fmt;
use std::fmt::Display;
use crate::pattern::*;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeVariable {
    pub reference: Reference,
    pub type_: Option<TypeConstraint>,
}

impl TypeVariable {
    pub fn into_pattern(self) -> Pattern {
        self.into_bound_variable().into_pattern()
    }

    pub fn into_bound_variable(self) -> BoundVariable {
        BoundVariable::Type(self)
    }

    pub fn new(reference: Reference) -> TypeVariable {
        TypeVariable {
            reference,
            type_: None,
        }
    }
}

impl TypeVariableBuilder for TypeVariable {
    fn constrain_type(mut self, constraint: TypeConstraint) -> TypeVariable {
        self.type_ = Some(constraint);
        self
    }
}

impl Display for TypeVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)?;
        if let Some(type_) = &self.type_ {
            write!(f, " {}", type_)?;
        }
        Ok(())
    }
}
