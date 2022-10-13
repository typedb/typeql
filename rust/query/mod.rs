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

use crate::{enum_getter, pattern::*, var, ErrorMessage};

mod typeql_delete;
pub use typeql_delete::*;

mod typeql_insert;
pub use typeql_insert::*;

mod typeql_match;
pub use typeql_match::*;

mod typeql_update;
pub use typeql_update::*;

mod writable;
pub use writable::*;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Match(TypeQLMatch),
    Insert(TypeQLInsert),
    Delete(TypeQLDelete),
    Update(TypeQLUpdate),
}

impl Query {
    enum_getter!(into_match, Match, TypeQLMatch);
    enum_getter!(into_insert, Insert, TypeQLInsert);
    enum_getter!(into_delete, Delete, TypeQLDelete);
    enum_getter!(into_update, Update, TypeQLUpdate);
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Query::*;
        match self {
            Match(query) => write!(f, "{}", query),
            Insert(query) => write!(f, "{}", query),
            Delete(query) => write!(f, "{}", query),
            Update(query) => write!(f, "{}", query),
        }
    }
}
