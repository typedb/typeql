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
pub use builder::{contains, eq, gt, gte, like, lt, lte, neq, not, rel, rule, type_, var};

pub mod common;
pub mod parser;
pub mod pattern;
pub mod query;

#[macro_use]
mod util;

use crate::{common::error::ErrorReport, pattern::Definable};
use antlr_rust::{common_token_stream::CommonTokenStream, InputStream, Parser as ANTLRParser};
use common::error::ErrorMessage;
use parser::{
    error_listener::ErrorListener, syntax_error::SyntaxError, visit_eof_definables,
    visit_eof_label, visit_eof_pattern, visit_eof_patterns, visit_eof_queries, visit_eof_query,
    visit_eof_schema_rule, visit_eof_variable,
};
use pattern::{Label, Pattern, RuleDefinition, Variable};
use query::Query;
use std::{cell::RefCell, rc::Rc};
use typeql_grammar::{typeqlrustlexer::TypeQLRustLexer, typeqlrustparser::TypeQLRustParser};

fn trim_start_comments(mut s: &str) -> &str {
    while s.starts_with('#') {
        s = s.trim_start_matches(|c| c != '\n').trim_start();
    }
    s
}

macro_rules! parse {
    ($visitor:ident($accessor:ident($input:ident))) => {{
        let input = trim_start_comments($input.trim());
        let lexer = TypeQLRustLexer::new(InputStream::new(input));
        let mut parser = TypeQLRustParser::new(CommonTokenStream::new(lexer));

        parser.remove_error_listeners();
        let errors = Rc::new(RefCell::new(Vec::<SyntaxError>::new()));
        parser.add_error_listener(Box::new(ErrorListener::new(input, errors.clone())));

        let ast_root = parser.$accessor().unwrap();

        if errors.borrow().is_empty() {
            $visitor(ast_root)
        } else {
            Err(errors.take().into_iter().map(SyntaxError::into).collect::<Vec<_>>().into())
        }
    }};
}

pub fn parse_query(typeql_query: &str) -> Result<Query, ErrorReport> {
    parse!(visit_eof_query(eof_query(typeql_query)))
}

pub fn parse_queries(
    typeql_queries: &str,
) -> Result<impl Iterator<Item = Result<Query, ErrorReport>> + '_, ErrorReport> {
    parse!(visit_eof_queries(eof_queries(typeql_queries)))
}

pub fn parse_pattern(typeql_pattern: &str) -> Result<Pattern, ErrorReport> {
    parse!(visit_eof_pattern(eof_pattern(typeql_pattern)))
}

pub fn parse_patterns(typeql_patterns: &str) -> Result<Vec<Pattern>, ErrorReport> {
    parse!(visit_eof_patterns(eof_patterns(typeql_patterns)))
}

pub fn parse_definables(typeql_definables: &str) -> Result<Vec<Definable>, ErrorReport> {
    parse!(visit_eof_definables(eof_definables(typeql_definables)))
}

pub fn parse_rule(typeql_rule: &str) -> Result<RuleDefinition, ErrorReport> {
    parse!(visit_eof_schema_rule(eof_schema_rule(typeql_rule)))
}

pub fn parse_variable(typeql_variable: &str) -> Result<Variable, ErrorReport> {
    parse!(visit_eof_variable(eof_variable(typeql_variable)))
}

pub fn parse_label(typeql_label: &str) -> Result<Label, ErrorReport> {
    parse!(visit_eof_label(eof_label(typeql_label)))
}
