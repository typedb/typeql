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

use std::cell::RefCell;
use std::convert::Into;
use std::fmt::{Display, Formatter};
use std::ops::Deref;
use std::rc::Rc;

use typeql_grammar::typeqlrustlexer::TypeQLRustLexer;
use typeql_grammar::typeqlrustparser::*;
use typeql_grammar::typeqlrustvisitor::TypeQLRustVisitorCompat;

use antlr_rust::common_token_stream::CommonTokenStream;
use antlr_rust::errors::ANTLRError;
use antlr_rust::recognizer::Recognizer;
use antlr_rust::token_factory::TokenFactory;
use antlr_rust::{error_listener::ErrorListener, InputStream, Parser};

pub mod parser;
pub mod pattern;
pub mod query;

#[macro_use]
mod util;

use crate::parser::TypeQLParser;
use pattern::*;
use query::*;

pub fn parse_query(typeql_query: &str) -> Query {
    parse_eof_query(typeql_query)
}

pub fn typeql_match(pattern: impl Into<Conjunction>) -> Query {
    Query::Match(TypeQLMatch {
        conjunction: pattern.into(),
        filter: vec![],
    })
}

pub fn var(name: impl Into<String>) -> UnboundVariable {
    UnboundVariable::named(name.into())
}

pub fn type_(name: impl Into<String>) -> TypeVariable {
    UnboundVariable::hidden().type_(name.into())
}

pub fn rel<T: Into<RolePlayerConstraint>>(value: T) -> ThingVariable {
    UnboundVariable::hidden().rel(value)
}

struct TypeQLSyntaxError {
    query_line: Option<String>,
    line: usize,
    char_position_in_line: usize,
    message: String,
}

impl Display for TypeQLSyntaxError {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        if let Some(query_line) = &self.query_line {
            // Error message appearance:
            //
            // syntax error at line 1:
            // match $
            //       ^
            // blah blah antlr blah
            write!(
                f,
                "There is a syntax error at line {}:\n{}\n{}^\n{}",
                self.line,
                query_line,
                " ".repeat(self.char_position_in_line),
                self.message
            )
        } else {
            write!(
                f,
                "There is a syntax error at line {}:\n{}",
                self.line, self.message
            )
        }
    }
}

struct TypeQLErrorListener {
    query_lines: Vec<String>,
    errors: Rc<RefCell<Vec<TypeQLSyntaxError>>>,
}

impl TypeQLErrorListener {
    fn new(query: &str, error_buffer: Rc<RefCell<Vec<TypeQLSyntaxError>>>) -> Self {
        Self {
            query_lines: query.lines().map(String::from).collect::<Vec<String>>(),
            errors: error_buffer,
        }
    }
}

impl<'a, T: Recognizer<'a>> ErrorListener<'a, T> for TypeQLErrorListener {
    fn syntax_error(
        &self,
        _: &T,
        _: Option<&<T::TF as TokenFactory<'a>>::Inner>,
        line: isize,
        column: isize,
        message: &str,
        _: Option<&ANTLRError>,
    ) {
        self.errors.deref().borrow_mut().push(TypeQLSyntaxError {
            query_line: self
                .query_lines
                .get::<usize>((line - 1).try_into().unwrap())
                .cloned(),
            line: line.try_into().unwrap(),
            char_position_in_line: column.try_into().unwrap(),
            message: String::from(message),
        });
    }
}

pub fn parse_eof_query(query_string: &str) -> Query {
    let lexer = TypeQLRustLexer::new(InputStream::new(query_string.into()));
    let mut parser = TypeQLRustParser::new(CommonTokenStream::new(lexer));

    parser.remove_error_listeners();
    let errors = Rc::new(RefCell::new(Vec::<TypeQLSyntaxError>::new()));
    parser.add_error_listener(Box::new(TypeQLErrorListener::new(
        query_string,
        errors.clone(),
    )));

    let query = TypeQLParser::default()
        .visit_eof_query(parser.eof_query().unwrap().as_ref())
        .into_query();

    for e in errors.deref().borrow().iter() {
        println!("{}", e);
    }
    query
}
