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
use crate::write_joined;
use std::fmt;
use std::fmt::{Display, Write};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeVariable {
    pub reference: Reference,
    pub label: Option<LabelConstraint>,
    pub relates: Vec<RelatesConstraint>,
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
            label: None,
            relates: vec![],
        }
    }
}

impl TypeVariableBuilder for TypeVariable {
    fn constrain_type(mut self, constraint: TypeConstraint) -> TypeVariable {
        use TypeConstraint::*;
        match constraint {
            Label(label) => self.label = Some(label),
            Relates(relates) => self.relates.push(relates),
        }
        self
    }
}

impl Display for TypeVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.reference.is_visible() {
            write!(f, "{}", self.reference)?;
            if let Some(type_) = &self.label {
                write!(f, " {}", type_)?;
            }
        } else {
            write!(f, "{}", self.label.as_ref().unwrap().scoped_type)?;
        }
        if !self.relates.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, self.relates, ",\n    ")?;
        }
        Ok(())
    }
}
