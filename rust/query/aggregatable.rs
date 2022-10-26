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

use crate::{common::token::Aggregate, UnboundVariable};

pub trait Aggregating<T> {
    fn new_count(base: T) -> Self;
    fn new(base: T, method: Aggregate, var: UnboundVariable) -> Self;
}

pub trait Aggregatable: Sized {
    type Aggregate: Aggregating<Self>;

    fn count(self) -> Self::Aggregate {
        Self::Aggregate::new_count(self)
    }

    fn aggregate(self, method: Aggregate, var: UnboundVariable) -> Self::Aggregate {
        Self::Aggregate::new(self, method, var)
    }

    fn max(self, var: impl Into<UnboundVariable>) -> Self::Aggregate {
        self.aggregate(Aggregate::Max, var.into())
    }

    fn min(self, var: impl Into<UnboundVariable>) -> Self::Aggregate {
        self.aggregate(Aggregate::Min, var.into())
    }

    fn mean(self, var: impl Into<UnboundVariable>) -> Self::Aggregate {
        self.aggregate(Aggregate::Mean, var.into())
    }

    fn median(self, var: impl Into<UnboundVariable>) -> Self::Aggregate {
        self.aggregate(Aggregate::Median, var.into())
    }

    fn std(self, var: impl Into<UnboundVariable>) -> Self::Aggregate {
        self.aggregate(Aggregate::Std, var.into())
    }

    fn sum(self, var: impl Into<UnboundVariable>) -> Self::Aggregate {
        self.aggregate(Aggregate::Sum, var.into())
    }
}
