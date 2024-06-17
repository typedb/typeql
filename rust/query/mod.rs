/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{typeql_define::TypeQLDefine, typeql_undefine::TypeQLUndefine};
use crate::{
    common::{Span, Spanned},
    enum_getter,
};

mod typeql_define;
mod typeql_undefine;

#[derive(Debug, Eq, PartialEq)]
pub enum SchemaQuery {
    Define(TypeQLDefine),
    Undefine(TypeQLUndefine),
}

enum_getter! { SchemaQuery
    into_define(Define) => TypeQLDefine,
    into_undefine(Undefine) => TypeQLUndefine,
}

impl Spanned for SchemaQuery {
    fn span(&self) -> Option<Span> {
        match self {
            SchemaQuery::Define(define) => define.span(),
            SchemaQuery::Undefine(undefine) => undefine.span(),
        }
    }
}

impl fmt::Display for SchemaQuery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Define(define_query) => fmt::Display::fmt(define_query, f),
            Self::Undefine(undefine_query) => fmt::Display::fmt(undefine_query, f),
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct DataQuery {
}

impl DataQuery {
    pub fn new() -> Self {
        Self {  }
    }
}

impl Spanned for DataQuery {
    fn span(&self) -> Option<Span> {
        todo!()
    }
}

impl fmt::Display for DataQuery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Schema(SchemaQuery),
    Data(DataQuery),
}

enum_getter! { Query
    into_schema(Schema) => SchemaQuery,
    into_data(Data) => DataQuery,
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Schema(schema_query) => fmt::Display::fmt(schema_query, f),
            Self::Data(data_query) => fmt::Display::fmt(data_query, f),
        }
    }
}
