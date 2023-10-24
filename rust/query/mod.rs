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

pub use match_clause::MatchClause;
pub use modifier::Limit;
pub use modifier::Offset;
pub use modifier::Sorting;
pub use modifier::sorting;
pub use typeql_define::TypeQLDefine;
pub use typeql_delete::TypeQLDelete;
pub use typeql_fetch::{Projection, ProjectionAttribute, ProjectionKeyLabel, ProjectionKeyVar, ProjectionSubquery, TypeQLFetch};
pub use typeql_get::{Filter, TypeQLGet};
pub use typeql_get_aggregate::{AggregateQueryBuilder, TypeQLGetAggregate, TypeQLGetGroupAggregate};
pub use typeql_get_group::TypeQLGetGroup;
pub use typeql_insert::TypeQLInsert;
pub use typeql_undefine::TypeQLUndefine;
pub use typeql_update::TypeQLUpdate;
pub use writable::Writable;

use crate::{
    common::{Result, validatable::Validatable},
    enum_getter, enum_wrapper,
};

mod match_clause;
mod typeql_get_aggregate;
mod typeql_define;
mod typeql_delete;
mod typeql_fetch;
mod typeql_get;
mod typeql_get_group;
mod typeql_insert;
mod typeql_undefine;
mod typeql_update;
mod writable;
pub(crate) mod modifier;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Define(TypeQLDefine),
    Undefine(TypeQLUndefine),
    Insert(TypeQLInsert),
    Delete(TypeQLDelete),
    Update(TypeQLUpdate),
    GetAggregate(TypeQLGetAggregate),
    Get(TypeQLGet),
    GetGroup(TypeQLGetGroup),
    GetGroupAggregate(TypeQLGetGroupAggregate),
    Fetch(TypeQLFetch),
}

enum_getter! { Query
    into_define(Define) => TypeQLDefine,
    into_undefine(Undefine) => TypeQLUndefine,
    into_insert(Insert) => TypeQLInsert,
    into_delete(Delete) => TypeQLDelete,
    into_update(Update) => TypeQLUpdate,
    into_get(Get) => TypeQLGet,
    into_get_aggregate(GetAggregate) => TypeQLGetAggregate,
    into_get_group(GetGroup) => TypeQLGetGroup,
    into_get_group_aggregate(GetGroupAggregate) => TypeQLGetGroupAggregate,
    into_fetch(Fetch) => TypeQLFetch,
}

enum_wrapper! { Query
    TypeQLDefine => Define,
    TypeQLUndefine => Undefine,
    TypeQLInsert => Insert,
    TypeQLDelete => Delete,
    TypeQLUpdate => Update,
    TypeQLGet => Get,
    TypeQLGetAggregate => GetAggregate,
    TypeQLGetGroup => GetGroup,
    TypeQLGetGroupAggregate => GetGroupAggregate,
    TypeQLFetch => Fetch
}

impl Validatable for Query {
    fn validate(&self) -> Result {
        match self {
            Query::Define(query) => query.validate(),
            Query::Undefine(query) => query.validate(),
            Query::Insert(query) => query.validate(),
            Query::Delete(query) => query.validate(),
            Query::Update(query) => query.validate(),
            Query::Get(query) => query.validate(),
            Query::GetAggregate(query) => query.validate(),
            Query::GetGroup(query) => query.validate(),
            Query::GetGroupAggregate(query) => query.validate(),
            Query::Fetch(query) => query.validate(),
        }
    }

    fn validated(self) -> Result<Self> {
        match self {
            Query::Define(query) => query.validated().map(TypeQLDefine::into),
            Query::Undefine(query) => query.validated().map(TypeQLUndefine::into),
            Query::Insert(query) => query.validated().map(TypeQLInsert::into),
            Query::Delete(query) => query.validated().map(TypeQLDelete::into),
            Query::Update(query) => query.validated().map(TypeQLUpdate::into),
            Query::Get(query) => query.validated().map(TypeQLGet::into),
            Query::GetAggregate(query) => query.validated().map(TypeQLGetAggregate::into),
            Query::GetGroup(query) => query.validated().map(TypeQLGetGroup::into),
            Query::GetGroupAggregate(query) => query.validated().map(TypeQLGetGroupAggregate::into),
            Query::Fetch(query) => query.validated().map(TypeQLFetch::into),
        }
    }
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Query::Define(query) => write!(f, "{query}"),
            Query::Undefine(query) => write!(f, "{query}"),
            Query::Insert(query) => write!(f, "{query}"),
            Query::Delete(query) => write!(f, "{query}"),
            Query::Update(query) => write!(f, "{query}"),
            Query::Get(query) => write!(f, "{query}"),
            Query::GetAggregate(query) => write!(f, "{query}"),
            Query::GetGroup(query) => write!(f, "{query}"),
            Query::GetGroupAggregate(query) => write!(f, "{query}"),
            Query::Fetch(query) => write!(f, "{query}"),
        }
    }
}
