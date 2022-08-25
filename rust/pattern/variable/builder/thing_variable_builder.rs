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

pub trait ThingVariableBuilderCommon: Sized {
    fn has(self, type_name: impl Into<String>, value: impl Into<Value>) -> ThingVariable {
        self.constrain_thing(
            HasConstraint::from_value(
                type_name.into(),
                ValueConstraint::new(Predicate::Equality(EqualityPredicate::Eq), value.into()),
            )
            .into_thing_constraint(),
        )
    }

    fn isa(self, type_name: impl Into<String>) -> ThingVariable {
        self.constrain_thing(
            IsaConstraint {
                type_name: type_name.into(),
                is_explicit: false,
            }
            .into_thing_constraint(),
        )
    }

    fn constrain_thing(self, constraint: ThingConstraint) -> ThingVariable;
}
