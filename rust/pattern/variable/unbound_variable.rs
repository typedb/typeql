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
pub struct UnboundVariable {
    pub reference: Reference,
}

impl UnboundVariable {
    pub fn into_pattern(self) -> Pattern {
        self.into_variable().into_pattern()
    }

    pub fn into_variable(self) -> Variable {
        Variable::Unbound(self)
    }

    pub fn into_thing(self) -> ThingVariable {
        ThingVariable::new(self.reference)
    }

    pub fn into_type(self) -> TypeVariable {
        TypeVariable::new(self.reference)
    }

    pub fn named(name: String) -> UnboundVariable {
        UnboundVariable {
            reference: Reference::Name(name),
        }
    }

    pub fn anonymous() -> UnboundVariable {
        UnboundVariable {
            reference: Reference::Anonymous(Visibility::Visible),
        }
    }

    pub fn hidden() -> UnboundVariable {
        UnboundVariable {
            reference: Reference::Anonymous(Visibility::Invisible),
        }
    }
}

impl ThingVariableBuilder for UnboundVariable {
    fn constrain_thing(self, constraint: ThingConstraint) -> ThingVariable {
        self.into_thing().constrain_thing(constraint)
    }
}

impl RelationVariableBuilder for UnboundVariable {
    fn constrain_role_player(self, constraint: RolePlayerConstraint) -> ThingVariable {
        self.into_thing().constrain_role_player(constraint)
    }
}

impl TypeVariableBuilder for UnboundVariable {
    fn constrain_type(self, constraint: TypeConstraint) -> TypeVariable {
        self.into_type().constrain_type(constraint)
    }
}

impl Display for UnboundVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)
    }
}
