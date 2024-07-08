/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use self::data::stage::{Match, Stage};
pub use self::{data::DataQuery, schema::SchemaQuery};
use crate::util::enum_getter;

pub mod data;
pub mod schema;

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

impl From<Match> for Query {
    fn from(value: Match) -> Self {
        Self::Data(DataQuery::new(None, Vec::new(), vec![Stage::Match(value)]))
    }
}
