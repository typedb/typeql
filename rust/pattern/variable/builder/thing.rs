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
    common::{error::ErrorMessage, token::Predicate},
    pattern::{
        HasConstraint, IIDConstraint, IsaConstraint, RelationConstraint, RolePlayerConstraint,
        ThingVariable, Value, ValueConstraint,
    },
};

pub trait ThingConstrainable {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable;
    fn constrain_iid(self, iid: IIDConstraint) -> ThingVariable;
    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable;
    fn constrain_value(self, value: ValueConstraint) -> ThingVariable;
    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable;
}

pub trait ThingVariableBuilder {
    fn has<T: TryInto<HasConstraint>>(self, has: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<HasConstraint>>::Error>;

    fn iid<T: TryInto<IIDConstraint>>(self, iid: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<IIDConstraint>>::Error>;

    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingVariable;

    fn eq(self, value: impl Into<Value>) -> ThingVariable;
    fn neq(self, value: impl Into<Value>) -> ThingVariable;
    fn gt(self, value: impl Into<Value>) -> ThingVariable;
    fn gte(self, value: impl Into<Value>) -> ThingVariable;
    fn lt(self, value: impl Into<Value>) -> ThingVariable;
    fn lte(self, value: impl Into<Value>) -> ThingVariable;
    fn contains(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage>;
    fn like(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage>;
}

impl<U: ThingConstrainable> ThingVariableBuilder for U {
    fn has<T: TryInto<HasConstraint>>(self, has: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<HasConstraint>>::Error>,
    {
        Ok(self.constrain_has(has.try_into()?))
    }

    fn iid<T: TryInto<IIDConstraint>>(self, iid: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<IIDConstraint>>::Error>,
    {
        Ok(self.constrain_iid(iid.try_into()?))
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingVariable {
        self.constrain_isa(isa.into())
    }

    fn eq(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_value(ValueConstraint::new(Predicate::Eq, value.into()).unwrap())
    }

    fn neq(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_value(ValueConstraint::new(Predicate::Neq, value.into()).unwrap())
    }

    fn gt(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_value(ValueConstraint::new(Predicate::Gt, value.into()).unwrap())
    }

    fn gte(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_value(ValueConstraint::new(Predicate::Gte, value.into()).unwrap())
    }

    fn lt(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_value(ValueConstraint::new(Predicate::Lt, value.into()).unwrap())
    }

    fn lte(self, value: impl Into<Value>) -> ThingVariable {
        self.constrain_value(ValueConstraint::new(Predicate::Lte, value.into()).unwrap())
    }

    fn contains(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(
            Predicate::Contains,
            Value::from(string.into()),
        )?))
    }

    fn like(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Like, Value::from(string.into()))?))
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
