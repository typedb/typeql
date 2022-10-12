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

use crate::common::error::ErrorMessage;
use crate::common::token::Predicate;
use crate::pattern::*;

pub trait ThingConstrainable {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable;
    fn constrain_iid(self, iid: IIDConstraint) -> ThingVariable;
    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable;
    fn constrain_value(self, value: ValueConstraint) -> ThingVariable;
    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable;
}

pub trait ThingVariableBuilder {
    fn has<T: TryInto<Value>>(
        self,
        type_name: impl Into<String>,
        value: T,
    ) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<Value>>::Error>;

    fn iid<T: TryInto<IIDConstraint>>(self, iid: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<IIDConstraint>>::Error>;

    fn isa(self, isa: impl Into<IsaConstraint>) -> Result<ThingVariable, ErrorMessage>;

    fn eq(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage>;
    fn neq(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage>;
    fn gt(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage>;
    fn gte(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage>;
    fn lt(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage>;
    fn lte(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage>;
    fn contains(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage>;
    fn like(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage>;
}

impl<U: ThingConstrainable> ThingVariableBuilder for U {
    fn has<T: TryInto<Value>>(
        self,
        type_name: impl Into<String>,
        value: T,
    ) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<Value>>::Error>,
    {
        Ok(self.constrain_has(match value.try_into()? {
            Value::Variable(variable) => HasConstraint::from((type_name.into(), *variable)),
            value => {
                HasConstraint::from((type_name.into(), ValueConstraint::new(Predicate::Eq, value)))
            }
        }))
    }

    fn iid<T: TryInto<IIDConstraint>>(self, iid: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<IIDConstraint>>::Error>,
    {
        Ok(self.constrain_iid(iid.try_into()?))
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_isa(isa.into()))
    }

    fn eq(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Eq, value.into())))
    }

    fn neq(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Neq, value.into())))
    }

    fn gt(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Gt, value.into())))
    }

    fn gte(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Gte, value.into())))
    }

    fn lt(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Lt, value.into())))
    }

    fn lte(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Lte, value.into())))
    }

    fn contains(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self
            .constrain_value(ValueConstraint::new(Predicate::Contains, Value::from(string.into()))))
    }

    fn like(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_value(ValueConstraint::new(Predicate::Like, Value::from(string.into()))))
    }
}

impl<U: ThingVariableBuilder> ThingVariableBuilder for Result<U, ErrorMessage> {
    fn has<T: TryInto<Value>>(
        self,
        type_name: impl Into<String>,
        value: T,
    ) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<Value>>::Error>,
    {
        self?.has(type_name, value)
    }

    fn iid<T: TryInto<IIDConstraint>>(self, iid: T) -> Result<ThingVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<IIDConstraint>>::Error>,
    {
        self?.iid(iid)
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> Result<ThingVariable, ErrorMessage> {
        self?.isa(isa)
    }

    fn eq(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        self?.eq(value)
    }

    fn neq(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        self?.neq(value)
    }

    fn gt(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        self?.gt(value)
    }

    fn gte(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        self?.gte(value)
    }

    fn lt(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        self?.lt(value)
    }

    fn lte(self, value: impl Into<Value>) -> Result<ThingVariable, ErrorMessage> {
        self?.lte(value)
    }

    fn contains(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage> {
        self?.contains(string)
    }

    fn like(self, string: impl Into<String>) -> Result<ThingVariable, ErrorMessage> {
        self?.like(string)
    }
}

pub trait RelationConstrainable {
    fn constrain_role_player(self, role_player: RolePlayerConstraint) -> ThingVariable;
}

pub trait RelationVariableBuilder {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> Result<ThingVariable, ErrorMessage>;
}

impl<U: RelationConstrainable> RelationVariableBuilder for U {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> Result<ThingVariable, ErrorMessage> {
        Ok(self.constrain_role_player(value.into()))
    }
}

impl<U: RelationVariableBuilder> RelationVariableBuilder for Result<U, ErrorMessage> {
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> Result<ThingVariable, ErrorMessage> {
        self?.rel(value)
    }
}
