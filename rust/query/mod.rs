/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use self::pipeline::stage::{Match, Stage};
pub use self::{
    pipeline::{stage, Pipeline},
    schema::SchemaQuery,
};
use crate::{common::error::TypeQLError::InvalidCasting, util::enum_getter};

pub mod pipeline;
pub mod schema;

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Schema(SchemaQuery, bool),
    Pipeline(Pipeline, bool),
}

impl Query {
    fn variant_name(&self) -> &'static str {
        match self {
            Self::Schema(_, _) => "Schema",
            Self::Pipeline(_, _) => "Pipeline",
        }
    }

    pub fn has_explicit_end(&self) -> bool {
        match self {
            Query::Schema(_, explicit_end) | Query::Pipeline(_, explicit_end) => *explicit_end,
        }
    }

    pub fn set_explicit_end(&mut self, explicit_end: bool) {
        match self {
            Query::Schema(_, end) => *end = explicit_end,
            Query::Pipeline(_, end) => *end = explicit_end,
        }
    }

    pub fn into_schema(self) -> SchemaQuery {
        match self {
            Self::Schema(schema, _) => schema,
            _ => panic!(
                "{}",
                InvalidCasting {
                    enum_name: stringify!(Query),
                    variant: self.variant_name(),
                    expected_variant: stringify!(Schema),
                    typename: stringify!(SchemaQuery),
                }
            ),
        }
    }
    pub fn into_pipeline(self) -> Pipeline {
        match self {
            Self::Pipeline(pipeline, _) => pipeline,
            _ => panic!(
                "{}",
                InvalidCasting {
                    enum_name: stringify!(Query),
                    variant: self.variant_name(),
                    expected_variant: stringify!(Pipeline),
                    typename: stringify!(Pipeline),
                }
            ),
        }
    }
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Schema(schema_query, _) => fmt::Display::fmt(schema_query, f),
            Self::Pipeline(data_query, _) => fmt::Display::fmt(data_query, f),
        }
    }
}

impl From<Match> for Query {
    fn from(value: Match) -> Self {
        Self::Pipeline(Pipeline::new(None, Vec::new(), vec![Stage::Match(value)]), false)
    }
}
