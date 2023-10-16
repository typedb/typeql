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

use crate::pattern::{AssignConstraint, PredicateConstraint, ValueStatement};

pub trait ValueConstrainable {
    fn constrain_assign(self, assign: AssignConstraint) -> ValueStatement;
    fn constrain_predicate(self, predicate: PredicateConstraint) -> ValueStatement;
}

pub trait ValueVariableBuilder: Sized {
    fn assign(self, assign: impl Into<AssignConstraint>) -> ValueStatement;
    fn predicate(self, predicate: impl Into<PredicateConstraint>) -> ValueStatement;
}

impl<U: ValueConstrainable> ValueVariableBuilder for U {
    fn assign(self, assign: impl Into<AssignConstraint>) -> ValueStatement {
        self.constrain_assign(assign.into())
    }
    fn predicate(self, predicate: impl Into<PredicateConstraint>) -> ValueStatement {
        self.constrain_predicate(predicate.into())
    }
}
