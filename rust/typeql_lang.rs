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

#[macro_use]
mod builder;
pub mod common;
pub mod parser;
pub mod pattern;
pub mod query;
#[macro_use]
mod util;

pub use builder::{abs, ceil, contains, constant, cvar, eq, floor, gt, gte, like, lt, lte, neq, not, rel, round, rule, type_, vvar};
use common::Result;
use parser::{
    visit_eof_definables, visit_eof_label, visit_eof_pattern, visit_eof_patterns, visit_eof_queries, visit_eof_query,
    visit_eof_schema_rule, visit_eof_variable,
};
use pattern::{Definable, Label, Pattern, RuleDefinition, Variable};
use query::Query;

pub fn parse_query(typeql_query: &str) -> Result<Query> {
    visit_eof_query(typeql_query.trim_end())
}

pub fn parse_queries(typeql_queries: &str) -> Result<impl Iterator<Item = Result<Query>> + '_> {
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

pub fn parse_rule(typeql_rule: &str) -> Result<RuleDefinition> {
    visit_eof_schema_rule(typeql_rule.trim_end())
}

pub fn parse_variable(typeql_variable: &str) -> Result<Variable> {
    visit_eof_variable(typeql_variable.trim_end())
}

pub fn parse_label(typeql_label: &str) -> Result<Label> {
    visit_eof_label(typeql_label)
}
