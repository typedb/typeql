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
    common::token::Command::Group, AggregateQueryBuilder, Query, TypeQLMatch, UnboundVariable,
};
use std::fmt;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLMatchGroup {
    pub query: TypeQLMatch,
    pub group_var: UnboundVariable,
}

impl AggregateQueryBuilder for TypeQLMatchGroup {}

impl TypeQLMatchGroup {
    pub fn into_query(self) -> Query {
        Query::Group(self)
    }
}

impl fmt::Display for TypeQLMatchGroup {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{} {};", self.query, Group, self.group_var)
    }
}
