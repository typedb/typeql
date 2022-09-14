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

pub trait TypeVariableBuilder: Sized {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable;
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable;
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable;
    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable;

    fn plays(self, plays: impl Into<PlaysConstraint>) -> BoundVariable {
        self.constrain_plays(plays.into()).into_bound_variable()
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> BoundVariable {
        self.constrain_relates(relates.into()).into_bound_variable()
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> BoundVariable {
        self.constrain_sub(sub.into()).into_bound_variable()
    }

    fn type_(self, type_name: impl Into<ScopedType>) -> BoundVariable {
        self.constrain_label(LabelConstraint { scoped_type: type_name.into() })
            .into_bound_variable()
    }
}
