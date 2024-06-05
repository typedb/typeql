/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

#![deny(elided_lifetimes_in_paths)]
#![deny(unused_must_use)]

#[macro_use]
mod builder;
pub mod common;
pub mod parser;
pub mod pattern;
pub mod query;
pub mod variable;
#[macro_use]
mod util;

pub use builder::{
    abs, ceil, constant, contains, cvar, eq, floor, gt, gte, label, like, lt, lte, neq, rel, round, rule, type_,
};
use common::Result;
// use parser::{
//     visit_eof_definables, visit_eof_label, visit_eof_pattern, visit_eof_patterns, visit_eof_queries, visit_eof_query,
//     visit_eof_schema_rule, visit_eof_statement,
// };
use pattern::{Definable, Label, Pattern, Rule, Statement};
use query::Query;

// pub fn parse_query(typeql_query: &str) -> Result<Query> {
//     visit_eof_query(typeql_query.trim_end())
// }
//
// pub fn parse_queries(typeql_queries: &str) -> Result<impl Iterator<Item = Result<Query>> + '_> {
//     visit_eof_queries(typeql_queries.trim_end())
// }
//
// pub fn parse_pattern(typeql_pattern: &str) -> Result<Pattern> {
//     visit_eof_pattern(typeql_pattern.trim_end())
// }
//
// pub fn parse_patterns(typeql_patterns: &str) -> Result<Vec<Pattern>> {
//     visit_eof_patterns(typeql_patterns.trim_end())
// }
//
// pub fn parse_definables(typeql_definables: &str) -> Result<Vec<Definable>> {
//     visit_eof_definables(typeql_definables.trim_end())
// }
//
// pub fn parse_rule(typeql_rule: &str) -> Result<Rule> {
//     visit_eof_schema_rule(typeql_rule.trim_end())
// }
//
// pub fn parse_statement(typeql_statement: &str) -> Result<Statement> {
//     visit_eof_statement(typeql_statement.trim_end())
// }
//
// pub fn parse_label(typeql_label: &str) -> Result<Label> {
//     visit_eof_label(typeql_label)
// }
