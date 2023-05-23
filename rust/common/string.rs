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

pub(crate) fn quote(string: &str) -> String {
    format!("\"{string}\"")
}

pub(crate) fn unquote(quoted_string: &str) -> String {
    String::from(&quoted_string[1..quoted_string.len() - 1])
}

pub(crate) fn indent(multiline_string: &str) -> String {
    format!("    {}", multiline_string.replace('\n', "\n    "))
}

pub(crate) fn escape_regex(regex: &str) -> String {
    regex.replace('/', r#"\/"#)
}

pub(crate) fn unescape_regex(regex: &str) -> String {
    regex.replace(r#"\/"#, "/")
}

pub(crate) fn format_double(double: f64) -> String {
    let formatted = format!("{double:.12}").trim_end_matches('0').to_string();
    if formatted.ends_with('.') {
        formatted + "0"
    } else {
        formatted
    }
}
