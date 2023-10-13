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

mod match_clause;
mod typeql_get_aggregate;
mod typeql_define;
mod typeql_delete;
mod typeql_insert;
mod typeql_get;
mod typeql_get_group;
mod typeql_undefine;
mod typeql_update;
mod writable;
pub(crate) mod modifier;

use std::fmt;

pub use match_clause::MatchClause;
pub use modifier::Limit;
pub use modifier::Offset;
pub use modifier::Sorting;
pub use modifier::sorting;
pub use typeql_get_aggregate::{AggregateQueryBuilder, TypeQLGetAggregate, TypeQLGetGroupAggregate};
pub use typeql_define::TypeQLDefine;
pub use typeql_delete::TypeQLDelete;
pub use typeql_insert::TypeQLInsert;
pub use typeql_get::{Filter, TypeQLGet};
pub use typeql_get_group::TypeQLGetGroup;
pub use typeql_undefine::TypeQLUndefine;
pub use typeql_update::TypeQLUpdate;
pub use writable::Writable;

use crate::{
    common::{Result, validatable::Validatable},
    enum_getter, enum_wrapper,
};

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Get(TypeQLGet),
    Insert(TypeQLInsert),
    Delete(TypeQLDelete),
    Update(TypeQLUpdate),
    Define(TypeQLDefine),
    Undefine(TypeQLUndefine),
    Aggregate(TypeQLGetAggregate),
    Group(TypeQLGetGroup),
    GroupAggregate(TypeQLGetGroupAggregate),
}

enum_getter! { Query
    into_match(Get) => TypeQLGet,
    into_insert(Insert) => TypeQLInsert,
    into_delete(Delete) => TypeQLDelete,
    into_update(Update) => TypeQLUpdate,
    into_define(Define) => TypeQLDefine,
    into_undefine(Undefine) => TypeQLUndefine,
    into_aggregate(Aggregate) => TypeQLGetAggregate,
    into_group(Group) => TypeQLGetGroup,
    into_group_aggregate(GroupAggregate) => TypeQLGetGroupAggregate,
}

enum_wrapper! { Query
    TypeQLGet => Get,
    TypeQLInsert => Insert,
    TypeQLDelete => Delete,
    TypeQLUpdate => Update,
    TypeQLDefine => Define,
    TypeQLUndefine => Undefine,
    TypeQLGetAggregate => Aggregate,
    TypeQLGetGroup => Group,
    TypeQLGetGroupAggregate => GroupAggregate,
}

impl Validatable for Query {
    fn validate(&self) -> Result<()> {
        use Query::*;
        match self {
            Get(query) => query.validate(),
            Insert(query) => query.validate(),
            Delete(query) => query.validate(),
            Update(query) => query.validate(),
            Define(query) => query.validate(),
            Undefine(query) => query.validate(),
            Aggregate(query) => query.validate(),
            Group(query) => query.validate(),
            GroupAggregate(query) => query.validate(),
        }
    }

    fn validated(self) -> Result<Self> {
        use Query::*;
        match self {
            Get(query) => query.validated().map(TypeQLGet::into),
            Insert(query) => query.validated().map(TypeQLInsert::into),
            Delete(query) => query.validated().map(TypeQLDelete::into),
            Update(query) => query.validated().map(TypeQLUpdate::into),
            Define(query) => query.validated().map(TypeQLDefine::into),
            Undefine(query) => query.validated().map(TypeQLUndefine::into),
            Aggregate(query) => query.validated().map(TypeQLGetAggregate::into),
            Group(query) => query.validated().map(TypeQLGetGroup::into),
            GroupAggregate(query) => query.validated().map(TypeQLGetGroupAggregate::into),
        }
    }
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Query::*;
        match self {
            Get(query) => write!(f, "{query}"),
            Insert(query) => write!(f, "{query}"),
            Delete(query) => write!(f, "{query}"),
            Update(query) => write!(f, "{query}"),
            Define(query) => write!(f, "{query}"),
            Undefine(query) => write!(f, "{query}"),
            Aggregate(query) => write!(f, "{query}"),
            Group(query) => write!(f, "{query}"),
            GroupAggregate(query) => write!(f, "{query}"),
        }
    }
}
