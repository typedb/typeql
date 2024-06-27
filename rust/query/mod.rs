/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{data::DataQuery, schema::SchemaQuery};

pub mod data;
pub mod schema;


#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Schema(SchemaQuery),
    Data(DataQuery),
}

impl fmt::Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Schema(schema_query) => fmt::Display::fmt(schema_query, f),
            Self::Data(data_query) => fmt::Display::fmt(data_query, f),
        }
    }
}
