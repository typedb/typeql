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

#![allow(dead_code)]

use std::{cell::RefCell, convert::Into, rc::Rc};

use typeql_grammar::{typeqlrustlexer::TypeQLRustLexer, typeqlrustparser::*};

use antlr_rust::{common_token_stream::CommonTokenStream, InputStream, Parser as ANTLRParser};

#[macro_use]
pub mod builder;
pub use builder::*;

pub mod common;
pub mod parser;
pub mod pattern;
pub mod query;

#[macro_use]
mod util;

use crate::{
    common::error::ErrorMessage,
    parser::{error_listener::ErrorListener, syntax_error::SyntaxError, visit_eof_query},
};
use pattern::*;
use query::*;

pub fn parse_query(typeql_query: &str) -> Result<Query, String> {
    parse_eof_query(typeql_query.trim_end())
}

pub fn parse_eof_query(query_string: &str) -> Result<Query, String> {
    let lexer = TypeQLRustLexer::new(InputStream::new(query_string));
    let mut parser = TypeQLRustParser::new(CommonTokenStream::new(lexer));

    parser.remove_error_listeners();
    let errors = Rc::new(RefCell::new(Vec::<SyntaxError>::new()));
    parser.add_error_listener(Box::new(ErrorListener::new(query_string, errors.clone())));

    let query = visit_eof_query(parser.eof_query().unwrap());

    if errors.borrow().is_empty() {
        query.map_err(|em| em.message)
    } else {
        Err(errors
            .borrow()
            .iter()
            .map(SyntaxError::to_string)
            .collect::<Vec<String>>()
            .join("\n\n"))
    }
}
