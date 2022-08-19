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

extern crate core;

use std::convert::Into;

use typeql_grammar::typeqlrustlexer::TypeQLRustLexer;
use typeql_grammar::typeqlrustparser::*;
use typeql_grammar::typeqlrustvisitor::TypeQLRustVisitorCompat;

use antlr_rust::common_token_stream::CommonTokenStream;
use antlr_rust::InputStream;

pub mod parser;
pub mod query;
pub mod pattern;

use query::*;
use pattern::*;
use parser::Parser;

pub fn parse_query(typeql_query: &str) -> Query {
    parse_eof_query(typeql_query)
}

pub fn var<T: Into<String>>(name: T) -> UnboundVariable
{
    UnboundVariable {
        reference: Reference::Named(name.into()),
    }
}

pub fn typeql_match<T: Into<Conjunction>>(pattern: T) -> Query
{
    Query::Match(TypeQLMatch {
        conjunction: pattern.into(),
        filter: vec![],
    })
}

pub fn parse_eof_query(query_string: &str) -> Query {
    let lexer = TypeQLRustLexer::new(InputStream::new(query_string.into()));
    let mut parser = TypeQLRustParser::new(CommonTokenStream::new(lexer));
    Parser::default().visit_eof_query(parser.eof_query().unwrap().as_ref()).into_query()
}
