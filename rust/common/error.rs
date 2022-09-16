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

use std::convert::Infallible;

#[derive(Debug)]
pub struct ErrorMessage {
    pub code: usize,
    pub message: String,
}

impl From<Infallible> for ErrorMessage {
    fn from(_: Infallible) -> Self {
        unreachable!()
    }
}

pub struct ErrorTemplate {
    template: &'static str,
    prefix: &'static str,
    code: usize,
    padding_len: usize,
}

impl ErrorTemplate {
    pub fn format(&self, args: &[&str]) -> ErrorMessage {
        let expected_arg_count = self.template.matches("{}").count();
        assert_eq!(
            expected_arg_count,
            args.len(),
            "Message template `{:?}` takes `{}` args but `{}` were provided",
            self.template,
            expected_arg_count,
            args.len()
        );
        let mut buffer = format!("[{}{}{}] ", self.prefix, "0".repeat(self.padding_len), self.code);
        for (i, fragment) in self.template.split("{}").enumerate() {
            if i > 0 {
                buffer += args.get(i - 1).unwrap_or(&"{}");
            }
            buffer += fragment;
        }
        ErrorMessage { code: self.code, message: buffer }
    }
}

const fn num_digits(x: usize) -> usize {
    if x < 10 {
        1
    } else {
        1 + num_digits(x / 10)
    }
}

const fn max(x: usize, y: usize) -> usize {
    if x > y {
        x
    } else {
        y
    }
}

macro_rules! max {
    ($x:literal) => ($x);
    ($x:literal, $($xs:literal),*) => (max($x, max!($($xs),*)));
}

macro_rules! error_messages {
    {code: $code_pfx:literal, type: $message_pfx:literal, $($error_name:ident = $code:literal: $body:literal),* $(,)?} => {
        error_messages!($code_pfx, num_digits(max!($($code),*)), $message_pfx, $(($error_name, $code, $body)),*);
    };
    ($code_pfx:literal, $code_len:expr, $message_pfx:literal, $(($error_name:ident, $code:literal, $body:literal)),* $(,)?) => {
        $(
        pub const $error_name: ErrorTemplate = ErrorTemplate {
            template: concat!($message_pfx, ": ", $body),
            prefix: $code_pfx,
            code: $code,
            padding_len: $code_len - num_digits($code),
        };
        )*
    };
}

error_messages! {
   code: "TQL", type: "TypeQL Error",
   SYNTAX_ERROR_NO_DETAILS = 2: "There is a syntax error at line {}:\n{}",
   SYNTAX_ERROR_DETAILED = 3: "There is a syntax error at line {}:\n{}\n{}\n{}",
   INVALID_CASTING = 4: "The class '{}' cannot be casted to '{}'.",
   MISSING_PATTERNS = 5: "The query has not been provided with any patterns.",
   MISSING_DEFINABLES = 6: "The query has not been provided with any definables.",
   MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE = 7: "The match query does not have named variables to bound the nested disjunction/negation pattern(s).",
   MATCH_HAS_NO_NAMED_VARIABLE = 8: "The match query has no named variables to retrieve.",
   MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE = 9: "The pattern '{}' has no named variable.",
   MATCH_HAS_UNBOUNDED_NESTED_PATTERN = 10: "The match query contains a nested pattern is not bounded: '{}'.",
   MISSING_MATCH_FILTER = 11: "The match query cannot be constructed with NULL filter variable collection.",
   EMPTY_MATCH_FILTER = 12: "The match query cannot be filtered with an empty list of variables.",
   INVALID_IID_STRING = 13: "Invalid IID: '{}'. IIDs must follow the regular expression: '{}'.",
   INVALID_ATTRIBUTE_TYPE_REGEX = 14: "Invalid regular expression '{}'.",
   ILLEGAL_FILTER_VARIABLE_REPEATING = 15: "The variable '{}' occurred more than once in match query filter.",
   VARIABLE_OUT_OF_SCOPE_MATCH = 16: "The variable '{}' is out of scope of the match query.",
   VARIABLE_OUT_OF_SCOPE_DELETE = 17: "The deleted variable '{}' is out of scope of the match query.",
   NO_VARIABLE_IN_SCOPE_INSERT = 18: "None of the variables in 'insert' ('{}') is within scope of 'match' ('{}')",
   VARIABLE_NOT_NAMED = 19: "The variable '{}' is not named and cannot be used as a filter for match query.",
   INVALID_VARIABLE_NAME = 20: "The variable name '{}' is invalid; variables must match the following regular expression: '{}'.",
   ILLEGAL_CONSTRAINT_REPETITION = 21: "The variable '{}' contains illegally repeating constraints: '{}' and '{}'.",
   MISSING_CONSTRAINT_RELATION_PLAYER = 22: "A relation variable has not been provided with role players.",
   MISSING_CONSTRAINT_VALUE = 23: "A value constraint has not been provided with a variable or literal value.",
   MISSING_CONSTRAINT_PREDICATE = 24: "A value constraint has not been provided with a predicate.",
   INVALID_CONSTRAINT_DATETIME_PRECISION = 25: "Attempted to assign DateTime value of '{}' which is more precise than 1 millisecond.",
   INVALID_DEFINE_QUERY_VARIABLE = 26: "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.",
   INVALID_RULE_WHEN_MISSING_PATTERNS = 27: "Rule '{}' 'when' has not been provided with any patterns.",
   INVALID_RULE_WHEN_NESTED_NEGATION = 28: "Rule '{}' 'when' contains a nested negation.",
   INVALID_RULE_WHEN_CONTAINS_DISJUNCTION = 29: "Rule '{}' 'when' contains a disjunction.",
   INVALID_RULE_THEN = 30: "Rule '{}' 'then' '{}': must be exactly one attribute ownership, or exactly one relation.",
   INVALID_RULE_THEN_HAS = 31: "Rule '{}' 'then' '{}' tries to assign type '{}' to variable '{}', but this variable already had a type assigned by the rule 'when'. Try omitting this type assignment.",
   INVALID_RULE_THEN_VARIABLES = 32: "Rule '{}' 'then' variables must be present in rule 'when'.",
   REDUNDANT_NESTED_NEGATION = 33: "Invalid query containing redundant nested negations.",
   INVALID_SORTING_ORDER = 34: "Invalid sorting order. Valid options: '{}' or '{}'.",
   INVALID_COUNT_VARIABLE_ARGUMENT = 35: "Aggregate COUNT does not accept a Variable.",
   ILLEGAL_GRAMMAR = 36: "Illegal grammar: '{}'",
   ILLEGAL_CHAR_IN_LABEL = 37: "'{}' is not a valid Type label. Type labels must start with a letter, and may contain only letters, numbers, '-' and '_'.",
}
