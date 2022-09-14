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
use std::fmt::{Debug, Display};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum BoundVariable {
    Thing(ThingVariable),
    Type(TypeVariable),
}

impl BoundVariable {
    enum_getter!(into_thing, Thing, ThingVariable);
    enum_getter!(into_type, Type, TypeVariable);

    pub fn into_pattern(self) -> Pattern {
        self.into_variable().into_pattern()
    }

    pub fn into_variable(self) -> Variable {
        Variable::Bound(self)
    }
}

impl From<ThingVariable> for BoundVariable {
    fn from(thing: ThingVariable) -> Self {
        BoundVariable::Thing(thing)
    }
}

impl From<TypeVariable> for BoundVariable {
    fn from(type_: TypeVariable) -> Self {
        BoundVariable::Type(type_)
    }
}

impl ThingVariableBuilder for BoundVariable {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable {
        self.into_thing().constrain_has(has)
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

impl RelationVariableBuilder for BoundVariable {
    fn constrain_role_player(self, constraint: RolePlayerConstraint) -> ThingVariable {
        self.into_thing().constrain_role_player(constraint)
    }
}

impl TypeVariableBuilder for BoundVariable {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable {
        self.into_type().constrain_label(label)
    }

    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable {
        self.into_type().constrain_owns(owns)
    }

    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable {
        self.into_type().constrain_plays(plays)
    }

    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable {
        self.into_type().constrain_relates(relates)
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable {
        self.into_type().constrain_sub(sub)
    }
}

impl Display for BoundVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use BoundVariable::*;
        match self {
            Thing(thing) => write!(f, "{}", thing),
            Type(type_) => write!(f, "{}", type_),
        }
    }
}
