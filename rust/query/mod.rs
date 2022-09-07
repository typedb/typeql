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
use std::fmt::{Display, Formatter};

use crate::pattern::*;
use crate::{enum_getter, var};

mod typeql_match;
pub use typeql_match::*;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Match(TypeQLMatch),
    Dud(()),
}

impl Query {
    enum_getter!(into_match, Match, TypeQLMatch);

    pub fn get<T: Into<String>, const N: usize>(self, vars: [T; N]) -> Query {
        use Query::*;
        match self {
            Match(query) => Match(
                query.filter(
                    vars.into_iter()
                        .map(|s| UnboundVariable::named(s.into()).into_pattern())
                        .collect(),
                ),
            ),
            _ => todo!(),
        }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Query {
        use Query::*;
        match self {
            Match(query) => Match(query.sort(sorting)),
            _ => todo!(),
        }
    }

    pub fn limit(self, limit: usize) -> Query {
        use Query::*;
        match self {
            Match(query) => Match(query.limit(limit)),
            _ => todo!(),
        }
    }

    pub fn offset(self, offset: usize) -> Query {
        use Query::*;
        match self {
            Match(query) => Match(query.offset(offset)),
            _ => todo!(),
        }
    }
}

impl Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Query::*;
        match self {
            Match(query) => write!(f, "{}", query),
            _ => todo!(),
        }
    }
}
