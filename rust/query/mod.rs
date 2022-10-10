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

use crate::pattern::*;
use crate::{enum_getter, var, ErrorMessage};

mod typeql_insert;
pub use typeql_insert::*;

mod typeql_match;
pub use typeql_match::*;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Match(TypeQLMatch),
    Insert(TypeQLInsert),
}

impl Query {
    enum_getter!(into_match, Match, TypeQLMatch);
    enum_getter!(into_insert, Insert, TypeQLInsert);
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Query::*;
        match self {
            Match(query) => write!(f, "{}", query),
            Insert(query) => write!(f, "{}", query),
        }
    }
}

pub trait MatchQueryBuilder {
    fn get<T: Into<String>, const N: usize>(self, vars: [T; N]) -> Self;
    fn sort(self, sorting: impl Into<Sorting>) -> Self;
    fn limit(self, limit: usize) -> Self;
    fn offset(self, offset: usize) -> Self;
}

impl<U: MatchQueryBuilder> MatchQueryBuilder for Result<U, ErrorMessage> {
    fn get<T: Into<String>, const N: usize>(self, vars: [T; N]) -> Self {
        Ok(self?.get(vars))
    }

    fn sort(self, sorting: impl Into<Sorting>) -> Self {
        Ok(self?.sort(sorting))
    }

    fn limit(self, limit: usize) -> Self {
        Ok(self?.limit(limit))
    }

    fn offset(self, offset: usize) -> Self {
        Ok(self?.offset(offset))
    }
}
