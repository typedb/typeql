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
use std::fmt::{Display, Formatter};
use std::ops::Deref;
use std::rc::Rc;

use antlr_rust::errors::ANTLRError;
use antlr_rust::recognizer::Recognizer;
use antlr_rust::token_factory::TokenFactory;
use antlr_rust::error_listener::ErrorListener;

use crate::common::error::{ErrorMessage, SYNTAX_ERROR_DETAILED, SYNTAX_ERROR_NO_DETAILS};

pub struct TypeQLSyntaxError {
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
            f.write_str(
                &SYNTAX_ERROR_DETAILED.format(&[
                    self.line.to_string().as_str(),
                    query_line,
                    &(" ".repeat(self.char_position_in_line) + "^"),
                    &self.message
                ])
            )
        } else {
            f.write_str(
                &SYNTAX_ERROR_NO_DETAILS.format(&[
                    self.line.to_string().as_str(),
                    &self.message
                ])
            )
        }
    }
}

pub(crate) struct TypeQLErrorListener {
    query_lines: Vec<String>,
    errors: Rc<RefCell<Vec<TypeQLSyntaxError>>>,
}

impl TypeQLErrorListener {
    pub fn new(query: &str, error_buffer: Rc<RefCell<Vec<TypeQLSyntaxError>>>) -> Self {
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

