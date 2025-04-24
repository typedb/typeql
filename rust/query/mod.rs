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
use crate::{
    common::{error::TypeQLError::InvalidCasting, Span},
    pretty::Pretty,
    token, TypeRef,
};

pub mod pipeline;
pub mod schema;

#[derive(Debug, Eq, PartialEq)]
pub struct Query {
    span: Option<Span>,
    structure: QueryStructure,
    has_explicit_end: bool,
}

impl Query {
    pub(crate) fn new(span: Option<Span>, structure: QueryStructure, has_explicit_end: bool) -> Self {
        Self { span, structure, has_explicit_end }
    }

    pub fn has_explicit_end(&self) -> bool {
        self.has_explicit_end
    }

    pub fn into_structure(self) -> QueryStructure {
        self.structure
    }
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.structure, f)?;
        if self.has_explicit_end {
            if f.alternate() {
                write!(f, "\n{}", token::Clause::End)
            } else {
                write!(f, "{}", token::Clause::End)
            }
        } else {
            Ok(())
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum QueryStructure {
    Schema(SchemaQuery),
    Pipeline(Pipeline),
}

impl QueryStructure {
    fn variant_name(&self) -> &'static str {
        match self {
            Self::Schema(_) => "Schema",
            Self::Pipeline(_) => "Pipeline",
        }
    }

    pub fn into_schema(self) -> SchemaQuery {
        match self {
            Self::Schema(schema) => schema,
            _ => panic!(
                "{}",
                InvalidCasting {
                    enum_name: stringify!(QueryStructure),
                    variant: self.variant_name(),
                    expected_variant: stringify!(Schema),
                    typename: stringify!(SchemaQuery),
                }
            ),
        }
    }

    pub fn into_pipeline(self) -> Pipeline {
        match self {
            QueryStructure::Pipeline(pipeline) => pipeline,
            _ => panic!(
                "{}",
                InvalidCasting {
                    enum_name: stringify!(QueryStructure),
                    variant: self.variant_name(),
                    expected_variant: stringify!(Pipeline),
                    typename: stringify!(Pipeline),
                }
            ),
        }
    }
}

impl fmt::Display for QueryStructure {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Schema(schema_query) => fmt::Display::fmt(schema_query, f),
            Self::Pipeline(data_query) => fmt::Display::fmt(data_query, f),
        }
    }
}

impl From<Match> for Query {
    fn from(value: Match) -> Self {
        Self::new(None, QueryStructure::Pipeline(Pipeline::new(None, Vec::new(), vec![Stage::Match(value)])), false)
    }
}
