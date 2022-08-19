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

use crate::pattern::IsaConstraint;
use crate::pattern::Reference;
use crate::pattern::ThingVariable;
use crate::pattern::TypeVariable;
use crate::pattern::TypeConstraint;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct UnboundVariable {
    pub reference: Reference,
}

impl UnboundVariable {
    pub fn named(name: String) -> UnboundVariable {
        UnboundVariable { reference: Reference::Named(name) }
    }

    pub fn anonymous() -> UnboundVariable {
        UnboundVariable { reference: Reference::Anonymous(()) }
    }

    pub fn hidden() -> UnboundVariable {
        UnboundVariable { reference: Reference::Anonymous(()) }
    }

    pub fn into_thing(self) -> ThingVariable {
        ThingVariable::new(self.reference)
    }

    pub fn into_type(self) -> TypeVariable {
        TypeVariable::new(self.reference)
    }

    pub fn isa(self, type_name: &str) -> ThingVariable {
        self.into_thing().constrain(IsaConstraint {
            type_name: String::from(type_name),
            is_explicit: false,
        })
    }

    pub fn type_(self, type_name: &str) -> TypeVariable {
        TypeVariable::new(self.reference).constrain(TypeConstraint {
            type_name: String::from(type_name),
            is_explicit: false,
        })
    }
}
