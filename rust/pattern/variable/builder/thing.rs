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

use crate::{
    common::token,
    pattern::{
        HasConstraint, IIDConstraint, IsaConstraint, RelationConstraint, RolePlayerConstraint, ThingVariable, Value,
        Predicate,
    },
};

pub trait ThingConstrainable {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable;
    fn constrain_iid(self, iid: IIDConstraint) -> ThingVariable;
    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable;
    fn constrain_predicate(self, value: Predicate) -> ThingVariable;
    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable;
}

pub trait ThingVariableBuilder {
    fn has(self, has: impl Into<HasConstraint>) -> ThingVariable;
    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingVariable;
    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingVariable;
    fn eq(self, value: impl Into<Value>) -> ThingVariable;
    fn neq(self, value: impl Into<Value>) -> ThingVariable;
    fn gt(self, value: impl Into<Value>) -> ThingVariable;
    fn gte(self, value: impl Into<Value>) -> ThingVariable;
    fn lt(self, value: impl Into<Value>) -> ThingVariable;
    fn lte(self, value: impl Into<Value>) -> ThingVariable;
    fn contains(self, string: impl Into<String>) -> ThingVariable;
    fn like(self, string: impl Into<String>) -> ThingVariable;
}

impl<U: ThingConstrainable> ThingVariableBuilder for U {
    fn has(self, has: impl Into<HasConstraint>) -> ThingVariable {
        self.constrain_has(has.into())
    }

    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingVariable {
        self.constrain_iid(iid.into())
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingVariable {
        self.constrain_isa(isa.into())
    }

    fn eq(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Eq, value.into()))
    }

    fn neq(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Neq, value.into()))
    }

    fn gt(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Gt, value.into()))
    }

    fn gte(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Gte, value.into()))
    }

    fn lt(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Lt, value.into()))
    }

    fn lte(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Lte, value.into()))
    }

    fn contains(self, string: impl Into<String>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Contains, Value::from(string.into())))
    }

    fn like(self, string: impl Into<String>) -> ThingVariable {
        self.constrain_predicate(Predicate::new(token::Predicate::Like, Value::from(string.into())))
    }
}

pub trait RelationConstrainable {
    fn constrain_role_player(self, role_player: RolePlayerConstraint) -> ThingVariable;
}

pub trait RelationVariableBuilder {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> ThingVariable;
}

impl<U: RelationConstrainable> RelationVariableBuilder for U {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> ThingVariable {
        self.constrain_role_player(value.into())
    }
}
