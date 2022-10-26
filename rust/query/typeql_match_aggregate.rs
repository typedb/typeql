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

use std::fmt;

use crate::{
    common::token::{Aggregate, Aggregate::Count},
    query::*,
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLMatchAggregate {
    pub query: TypeQLMatch,
    pub method: Aggregate,
    pub var: Option<UnboundVariable>,
}

impl TypeQLMatchAggregate {
    pub fn new_count(query: TypeQLMatch) -> Self {
        Self { query, method: Count, var: None }
    }

    pub fn new(query: TypeQLMatch, method: Aggregate, var: UnboundVariable) -> Self {
        Self { query, method, var: Some(var) } // TODO check method is not COUNT & var is in query's scope
    }

    pub fn into_query(self) -> Query {
        Query::Aggregate(self)
    }
}

impl fmt::Display for TypeQLMatchAggregate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{}", self.query, self.method)?;
        if let Some(var) = &self.var {
            write!(f, " {}", var)?;
        }
        f.write_str(";")
    }
}
