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

mod reference;
pub use reference::*;

mod thing_variable;
pub use thing_variable::*;

mod type_variable;
pub use type_variable::*;

mod unbound_variable;
pub use unbound_variable::*;

mod builder;
pub use builder::*;

use crate::pattern::*;
use crate::ErrorMessage;
use std::fmt;
use std::fmt::Display;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Thing(ThingVariable),
    Type(TypeVariable),
    Unbound(UnboundVariable),
}

impl Variable {
    pub fn into_pattern(self) -> Pattern {
        Pattern::Variable(self)
    }

    pub fn into_type(self) -> TypeVariable {
        use Variable::*;
        match self {
            Type(var) => var,
            Unbound(var) => var.into_type(),
            _ => panic!(""),
        }
    }

    pub fn into_thing(self) -> ThingVariable {
        use Variable::*;
        match self {
            Thing(var) => var,
            Unbound(var) => var.into_thing(),
            _ => panic!(""),
        }
    }
}

impl From<UnboundVariable> for Variable {
    fn from(unbound: UnboundVariable) -> Self {
        Variable::Unbound(unbound)
    }
}

impl From<ThingVariable> for Variable {
    fn from(var: ThingVariable) -> Self {
        Variable::Thing(var)
    }
}

impl From<TypeVariable> for Variable {
    fn from(var: TypeVariable) -> Self {
        Variable::Type(var)
    }
}

impl ThingVariableBuilder for Variable {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable {
        self.into_thing().constrain_has(has)
    }

    fn constrain_iid(self, iid: IIDConstraint) -> ThingVariable {
        self.into_thing().constrain_iid(iid)
    }

    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable {
        self.into_thing().constrain_isa(isa)
    }

    fn constrain_value(self, value: ValueConstraint) -> ThingVariable {
        self.into_thing().constrain_value(value)
    }

    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable {
        self.into_thing().constrain_relation(relation)
    }
}

impl RelationVariableBuilder for Variable {
    fn constrain_role_player(self, constraint: RolePlayerConstraint) -> ThingVariable {
        self.into_thing().constrain_role_player(constraint)
    }
}

impl TypeVariableBuilder for Variable {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable {
        self.into_type().constrain_label(label)
    }

    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable {
        self.into_type().constrain_owns(owns)
    }

    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable {
        self.into_type().constrain_plays(plays)
    }

    fn constrain_regex(self, regex: RegexConstraint) -> TypeVariable {
        self.into_type().constrain_regex(regex)
    }

    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable {
        self.into_type().constrain_relates(relates)
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable {
        self.into_type().constrain_sub(sub)
    }
}

impl Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Variable::*;
        match self {
            Unbound(unbound) => write!(f, "{}", unbound),
            Thing(thing) => write!(f, "{}", thing),
            Type(type_) => write!(f, "{}", type_),
        }
    }
}
