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
use std::fmt::Display;

use crate::pattern::*;

mod typeql_match;
pub use typeql_match::*;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Match(TypeQLMatch),
    Dud(String),
}

impl Query {
    pub fn get<T: Into<String>>(self, var: T) -> Query {
        use Query::*;
        if let Match(mut query) = self {
            query.filter = vec![UnboundVariable {
                reference: Reference::Named(var.into()),
            }];
            Match(query)
        } else {
            panic!("")
        }
    }

    pub fn filter<T>(self, _: T) -> Query {
        self
    }
}

impl Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Query::*;
        match self {
            Match(query) => write!(f, "{}", query),
            Dud(message) => write!(f, "Dud({})", message),
        }
    }
}
