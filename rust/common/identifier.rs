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
 */

use std::sync::OnceLock;

use regex::{Regex, RegexBuilder};

const IDENTIFIER_CHAR: &str = "A-Za-z\
            \\u00C0-\\u00D6\
            \\u00D8-\\u00F6\
            \\u00F8-\\u02FF\
            \\u0370-\\u037D\
            \\u037F-\\u1FFF\
            \\u200C-\\u200D\
            \\u2070-\\u218F\
            \\u2C00-\\u2FEF\
            \\u3001-\\uD7FF\
            \\uF900-\\uFDCF\
            \\uFDF0-\\uFFFD";
const IDENTIFIER_CONNECTOR: &str = "_\
            \\-\
            \\u00B7\
            \\u0300-\\u036F\
            \\u203F-\\u2040";
const IDENTIFIER_DIGIT: &str = "0-9";

pub fn is_valid_label_identifier(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        let identifier_tail = format!(
            "{}{}{}",
            IDENTIFIER_CHAR,
            IDENTIFIER_CONNECTOR,
            IDENTIFIER_DIGIT
        );
        let identifier_pattern = format!("^[{}][{}]*$", IDENTIFIER_CHAR, identifier_tail);
        RegexBuilder::new(&identifier_pattern).build().unwrap()
    });
    regex.is_match(identifier)
}


pub fn is_valid_var_identifier(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        let identifier_start = format!(
            "{}{}",
            IDENTIFIER_CHAR,
            IDENTIFIER_DIGIT
        );
        let identifier_tail = format!(
            "{}{}{}",
            IDENTIFIER_CHAR,
            IDENTIFIER_DIGIT,
            IDENTIFIER_CONNECTOR
        );
        let identifier_pattern = format!("^[{}][{}]*$", identifier_start, identifier_tail);
        RegexBuilder::new(&identifier_pattern).build().unwrap()
    });
    regex.is_match(identifier)
}
