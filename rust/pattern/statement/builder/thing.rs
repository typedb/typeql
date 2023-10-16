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
        HasConstraint, IIDConstraint, IsaConstraint, PredicateConstraint, RelationConstraint, RolePlayerConstraint,
        ThingStatement, Value,
    },
};

pub trait ThingConstrainable {
    fn constrain_has(self, has: HasConstraint) -> ThingStatement;
    fn constrain_iid(self, iid: IIDConstraint) -> ThingStatement;
    fn constrain_isa(self, isa: IsaConstraint) -> ThingStatement;
    fn constrain_predicate(self, value: PredicateConstraint) -> ThingStatement;
    fn constrain_relation(self, relation: RelationConstraint) -> ThingStatement;
}

pub trait ThingVariableBuilder {
    fn has(self, has: impl Into<HasConstraint>) -> ThingStatement;
    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingStatement;
    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingStatement;
    fn eq(self, value: impl Into<Value>) -> ThingStatement;
    fn neq(self, value: impl Into<Value>) -> ThingStatement;
    fn gt(self, value: impl Into<Value>) -> ThingStatement;
    fn gte(self, value: impl Into<Value>) -> ThingStatement;
    fn lt(self, value: impl Into<Value>) -> ThingStatement;
    fn lte(self, value: impl Into<Value>) -> ThingStatement;
    fn contains(self, string: impl Into<String>) -> ThingStatement;
    fn like(self, string: impl Into<String>) -> ThingStatement;
}

impl<U: ThingConstrainable> ThingVariableBuilder for U {
    fn has(self, has: impl Into<HasConstraint>) -> ThingStatement {
        self.constrain_has(has.into())
    }

    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingStatement {
        self.constrain_iid(iid.into())
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingStatement {
        self.constrain_isa(isa.into())
    }

    fn eq(self, value: impl Into<Value>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Eq, value.into()))
    }

    fn neq(self, value: impl Into<Value>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Neq, value.into()))
    }

    fn gt(self, value: impl Into<Value>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Gt, value.into()))
    }

    fn gte(self, value: impl Into<Value>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Gte, value.into()))
    }

    fn lt(self, value: impl Into<Value>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Lt, value.into()))
    }

    fn lte(self, value: impl Into<Value>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Lte, value.into()))
    }

    fn contains(self, string: impl Into<String>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Contains, Value::from(string.into())))
    }

    fn like(self, string: impl Into<String>) -> ThingStatement {
        self.constrain_predicate(PredicateConstraint::new(token::Predicate::Like, Value::from(string.into())))
    }
}

pub trait RelationConstrainable {
    fn constrain_role_player(self, role_player: RolePlayerConstraint) -> ThingStatement;
}

pub trait RelationVariableBuilder {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> ThingStatement;
}

impl<U: RelationConstrainable> RelationVariableBuilder for U {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> ThingStatement {
        self.constrain_role_player(value.into())
    }
}
