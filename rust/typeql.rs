/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

#![deny(rust_2018_idioms)]
#![deny(rust_2021_compatibility)]
#![deny(rust_2024_compatibility)]
#![deny(elided_lifetimes_in_paths)]
#![deny(unused_must_use)]
#![allow(unused)]
#![warn(unused_imports)]

pub mod annotation;
pub mod builder;
pub mod common;
pub mod definition;
pub mod expression;
pub mod identifier;
pub mod parser;
pub mod pattern;
pub mod query;
#[macro_use]
mod util;

pub use common::Result;
use identifier::Label;
use parser::{
    visit_eof_definables, visit_eof_label, visit_eof_pattern, visit_eof_patterns, visit_eof_queries, visit_eof_query,
    visit_eof_statement,
};
use pattern::{Definable, Pattern, Statement};
use query::Query;

pub fn parse_query(typeql_query: &str) -> Result<Query> {
    visit_eof_query(typeql_query.trim_end())
}

pub fn parse_queries(typeql_queries: &str) -> Result<impl Iterator<Item = Query> + '_> {
    visit_eof_queries(typeql_queries.trim_end())
}

pub fn parse_pattern(typeql_pattern: &str) -> Result<Pattern> {
    visit_eof_pattern(typeql_pattern.trim_end())
}

pub fn parse_patterns(typeql_patterns: &str) -> Result<Vec<Pattern>> {
    visit_eof_patterns(typeql_patterns.trim_end())
}

pub fn parse_definables(typeql_definables: &str) -> Result<Vec<Definable>> {
    visit_eof_definables(typeql_definables.trim_end())
}

pub fn parse_statement(typeql_statement: &str) -> Result<Statement> {
    visit_eof_statement(typeql_statement.trim_end())
}

pub fn parse_label(typeql_label: &str) -> Result<Label> {
    visit_eof_label(typeql_label)
}
