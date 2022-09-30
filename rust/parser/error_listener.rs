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
use std::rc::Rc;

use antlr_rust::error_listener::ErrorListener as ANTLRErrorListener;
use antlr_rust::errors::ANTLRError;
use antlr_rust::recognizer::Recognizer;
use antlr_rust::token_factory::TokenFactory;

use crate::parser::syntax_error::SyntaxError;

pub(crate) struct ErrorListener {
    query_lines: Vec<String>,
    errors: Rc<RefCell<Vec<SyntaxError>>>,
}

impl ErrorListener {
    pub fn new(query: &str, errors: Rc<RefCell<Vec<SyntaxError>>>) -> Self {
        Self { query_lines: query.lines().map(String::from).collect::<Vec<String>>(), errors }
    }
}

impl<'a, T: Recognizer<'a>> ANTLRErrorListener<'a, T> for ErrorListener {
    fn syntax_error(
        &self,
        _: &T,
        _: Option<&<T::TF as TokenFactory<'a>>::Inner>,
        line: isize,
        column: isize,
        message: &str,
        _: Option<&ANTLRError>,
    ) {
        self.errors.borrow_mut().push(SyntaxError {
            query_line: self.query_lines.get::<usize>((line - 1).try_into().unwrap()).cloned(),
            line: line.try_into().unwrap(),
            char_position_in_line: column.try_into().unwrap(),
            message: String::from(message),
        });
    }
}
