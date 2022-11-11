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

use std::fmt;

use crate::common::error::{ErrorMessage, SYNTAX_ERROR_DETAILED, SYNTAX_ERROR_NO_DETAILS};

#[derive(Clone, Debug)]
pub struct SyntaxError {
    pub query_line: Option<String>,
    pub line: usize,
    pub char_position_in_line: usize,
    pub message: String,
}

impl From<SyntaxError> for ErrorMessage {
    fn from(syntax_error: SyntaxError) -> Self {
        if let Some(query_line) = &syntax_error.query_line {
            // Error message appearance:
            //
            // syntax error at line 1:
            // match $
            //       ^
            // blah blah antlr blah
            SYNTAX_ERROR_DETAILED.format(&[
                syntax_error.line.to_string().as_str(),
                query_line,
                &(" ".repeat(syntax_error.char_position_in_line) + "^"),
                &syntax_error.message,
            ])
        } else {
            SYNTAX_ERROR_NO_DETAILS
                .format(&[syntax_error.line.to_string().as_str(), &syntax_error.message])
        }
    }
}

impl fmt::Display for SyntaxError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", ErrorMessage::from(self.clone()).message)
    }
}
