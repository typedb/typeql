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
use crate::pattern::*;
use std::convert::Infallible;

pub trait ThingVariableBuilder: Sized {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable;
    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable;
    fn constrain_value(self, value: ValueConstraint) -> ThingVariable;
    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable;

    fn has<T: TryInto<Value>>(
        self,
        type_name: impl Into<String>,
        value: T,
    ) -> Result<BoundVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<Value>>::Error>,
    {
        Ok(self
            .constrain_has(match value.try_into()? {
                Value::Variable(variable) => {
                    HasConstraint::from_typed_variable(type_name.into(), *variable)
                }
                value => HasConstraint::from_value(
                    type_name.into(),
                    ValueConstraint::new(Predicate::Eq, value),
                ),
            })
            .into_bound_variable())
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> Result<BoundVariable, ErrorMessage> {
        Ok(self.constrain_isa(isa.into()).into_bound_variable())
    }

    fn eq(self, value: impl Into<Value>) -> Result<BoundVariable, ErrorMessage> {
        Ok(self
            .constrain_value(ValueConstraint::new(Predicate::Eq, value.into()))
            .into_bound_variable())
    }
}

impl<U: ThingVariableBuilder> ThingVariableBuilder for Result<U, ErrorMessage> {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable {
        match self {
            Ok(var) => var.constrain_has(has),
            Err(err) => panic!("{:?}", err),
        }
    }
    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable {
        match self {
            Ok(var) => var.constrain_isa(isa),
            Err(err) => panic!("{:?}", err),
        }
    }
    fn constrain_value(self, value: ValueConstraint) -> ThingVariable {
        match self {
            Ok(var) => var.constrain_value(value),
            Err(err) => panic!("{:?}", err),
        }
    }
    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable {
        match self {
            Ok(var) => var.constrain_relation(relation),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn has<T: TryInto<Value>>(
        self,
        type_name: impl Into<String>,
        value: T,
    ) -> Result<BoundVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<Value>>::Error>,
    {
        self?.has(type_name, value)
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> Result<BoundVariable, ErrorMessage> {
        self?.isa(isa)
    }

    fn eq(self, value: impl Into<Value>) -> Result<BoundVariable, ErrorMessage> {
        self?.eq(value)
    }
}

impl<U: ThingVariableBuilder> ThingVariableBuilder for Result<U, Infallible> {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable {
        self.unwrap().constrain_has(has)
    }
    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable {
        self.unwrap().constrain_isa(isa)
    }
    fn constrain_value(self, value: ValueConstraint) -> ThingVariable {
        self.unwrap().constrain_value(value)
    }
    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable {
        self.unwrap().constrain_relation(relation)
    }

    fn has<T: TryInto<Value>>(
        self,
        type_name: impl Into<String>,
        value: T,
    ) -> Result<BoundVariable, ErrorMessage>
    where
        ErrorMessage: From<<T as TryInto<Value>>::Error>,
    {
        self.unwrap().has(type_name, value)
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> Result<BoundVariable, ErrorMessage> {
        self.unwrap().isa(isa)
    }

    fn eq(self, value: impl Into<Value>) -> Result<BoundVariable, ErrorMessage> {
        self.unwrap().eq(value)
    }
}

pub trait RelationVariableBuilder: Sized {
    fn constrain_role_player(self, role_player: RolePlayerConstraint) -> ThingVariable;

    fn rel(self, value: impl Into<RolePlayerConstraint>) -> Result<BoundVariable, ErrorMessage> {
        Ok(self.constrain_role_player(value.into()).into_bound_variable())
    }
}

impl<U: RelationVariableBuilder> RelationVariableBuilder for Result<U, ErrorMessage> {
    fn constrain_role_player(self, role_player: RolePlayerConstraint) -> ThingVariable {
        match self {
            Ok(var) => var.constrain_role_player(role_player),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn rel(self, value: impl Into<RolePlayerConstraint>) -> Result<BoundVariable, ErrorMessage> {
        self?.rel(value)
    }
}

impl<U: RelationVariableBuilder> RelationVariableBuilder for Result<U, Infallible> {
    fn constrain_role_player(self, role_player: RolePlayerConstraint) -> ThingVariable {
        self.unwrap().constrain_role_player(role_player)
    }

    fn rel(self, value: impl Into<RolePlayerConstraint>) -> Result<BoundVariable, ErrorMessage> {
        self.unwrap().rel(value)
    }
}
