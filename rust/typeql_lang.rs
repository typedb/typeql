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

use std::{cell::RefCell, rc::Rc};

use typeql_grammar::{typeqlrustlexer::TypeQLRustLexer, typeqlrustparser::TypeQLRustParser};

use antlr_rust::{common_token_stream::CommonTokenStream, InputStream, Parser as ANTLRParser};

#[macro_use]
mod builder;
pub use builder::{contains, eq, gt, gte, like, lt, lte, neq, not, rel, rule, type_, var};

pub mod common;
pub mod parser;
pub mod pattern;
pub mod query;

#[macro_use]
mod util;

use common::error::ErrorMessage;
use parser::{
    error_listener::ErrorListener, syntax_error::SyntaxError, visit_eof_definables,
    visit_eof_label, visit_eof_pattern, visit_eof_patterns, visit_eof_queries, visit_eof_query,
    visit_eof_schema_rule, visit_eof_variable,
};
use pattern::{Label, Pattern, RuleDefinition, Variable};
use query::Query;

macro_rules! parse {
    ($visitor:ident($accessor:ident($input:expr))) => {{
        let input = $input;
        let lexer = TypeQLRustLexer::new(InputStream::new(input));
        let mut parser = TypeQLRustParser::new(CommonTokenStream::new(lexer));

        parser.remove_error_listeners();
        let errors = Rc::new(RefCell::new(Vec::<SyntaxError>::new()));
        parser.add_error_listener(Box::new(ErrorListener::new(input, errors.clone())));

        let result = $visitor(parser.$accessor().unwrap());

        if errors.borrow().is_empty() {
            result.map_err(|em| em.message)
        } else {
            Err(errors
                .borrow()
                .iter()
                .map(SyntaxError::to_string)
                .collect::<Vec<String>>()
                .join("\n\n"))
        }
    }};
}

pub fn parse_query(typeql_query: &str) -> Result<Query, String> {
    parse!(visit_eof_query(eof_query(typeql_query.trim_end())))
}

pub fn parse_queries(
    typeql_queries: &str,
) -> Result<impl Iterator<Item = Result<Query, ErrorMessage>> + '_, String> {
    parse!(visit_eof_queries(eof_queries(typeql_queries.trim_end())))
}

pub fn parse_pattern(typeql_pattern: &str) -> Result<Pattern, String> {
    parse!(visit_eof_pattern(eof_pattern(typeql_pattern.trim_end())))
}

pub fn parse_patterns(typeql_patterns: &str) -> Result<Vec<Pattern>, String> {
    parse!(visit_eof_patterns(eof_patterns(typeql_patterns.trim_end())))
}

pub fn parse_definables(typeql_definables: &str) -> Result<Vec<Pattern>, String> {
    parse!(visit_eof_definables(eof_definables(typeql_definables.trim_end())))
}

pub fn parse_rule(typeql_rule: &str) -> Result<RuleDefinition, String> {
    parse!(visit_eof_schema_rule(eof_schema_rule(typeql_rule.trim_end())))
}

pub fn parse_variable(typeql_variable: &str) -> Result<Variable, String> {
    parse!(visit_eof_variable(eof_variable(typeql_variable.trim_end())))
}

pub fn parse_label(typeql_label: &str) -> Result<Label, String> {
    parse!(visit_eof_label(eof_label(typeql_label.trim_end())))
}
