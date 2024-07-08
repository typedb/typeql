/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use self::pipeline::stage::{Match, Stage};
pub use self::{pipeline::Pipeline, schema::SchemaQuery};
use crate::util::enum_getter;

pub mod pipeline;
pub mod schema;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Schema(SchemaQuery),
    Pipeline(Pipeline),
}

enum_getter! { Query
    into_schema(Schema) => SchemaQuery,
    into_pipeline(Pipeline) => Pipeline,
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Schema(schema_query) => fmt::Display::fmt(schema_query, f),
            Self::Pipeline(data_query) => fmt::Display::fmt(data_query, f),
        }
    }
}

impl From<Match> for Query {
    fn from(value: Match) -> Self {
        Self::Pipeline(Pipeline::new(None, Vec::new(), vec![Stage::Match(value)]))
    }
}
