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

use crate::{
    common::token,
    pattern::{Label, Pattern, Reference, ThingVariable, TypeVariable, UnboundVariable, Value},
    write_joined,
};
use chrono::NaiveDateTime;
use pest::error::{Error as PestError, LineColLocation};
use std::fmt;

#[derive(Debug)]
pub struct Error {
    parse_errors: Vec<ErrorMessage>,
}

impl From<ErrorMessage> for Error {
    fn from(parse_error: ErrorMessage) -> Self {
        Self { parse_errors: vec![parse_error] }
    }
}

impl From<Vec<ErrorMessage>> for Error {
    fn from(parse_errors: Vec<ErrorMessage>) -> Self {
        assert!(!parse_errors.is_empty());
        Self { parse_errors }
    }
}

impl<T: pest::RuleType> From<PestError<T>> for Error {
    fn from(error: PestError<T>) -> Self {
        let (line, col) = match error.line_col {
            LineColLocation::Pos((line, col)) => (line, col),
            LineColLocation::Span((line, col), _) => (line, col),
        };
        Self::from(ErrorMessage::SyntaxErrorDetailed(
            line,
            error.line().to_owned(),
            " ".repeat(col - 1) + "^",
            error.variant.message().to_string(),
        ))
    }
}

impl fmt::Display for Error {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write_joined!(f, "\n\n", self.parse_errors)
    }
}

pub fn collect_err(i: &mut dyn Iterator<Item = Result<(), Error>>) -> Result<(), Error> {
    let messages = i.filter_map(Result::err).flat_map(|e| e.parse_errors).collect::<Vec<_>>();
    if messages.is_empty() {
        Ok(())
    } else {
        Err(Error { parse_errors: messages })
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

macro_rules! format_message {
    ($self:ident, $error_name:ident, $body:literal, ) => {
        format!($body)
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty) => {
        if let Self::$error_name(a) = &$self {
            format!($body, a)
        } else {
            unreachable!()
        }
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty) => {
        if let Self::$error_name(a, b) = &$self {
            format!($body, a, b)
        } else {
            unreachable!()
        }
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty, $t3:ty) => {
        if let Self::$error_name(a, b, c) = &$self {
            format!($body, a, b, c)
        } else {
            unreachable!()
        }
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty, $t3:ty, $t4:ty) => {
        if let Self::$error_name(a, b, c, d) = &$self {
            format!($body, a, b, c, d)
        } else {
            unreachable!()
        }
    };
}

macro_rules! error_messages {
    {$name:ident code: $code_pfx:literal, type: $message_pfx:literal,
    $(
        $error_name:ident( $($inner:ty),* $(,)? ) = $code:literal: $body:literal
    ),* $(,)?} => {
        error_messages!(
            $name, $code_pfx, num_digits(max!($($code),*)), $message_pfx,
            $(($error_name, $code, $body, ($($inner),*))),*
        );
    };

    (
        $name:ident, $code_pfx:literal, $code_len:expr, $message_pfx:literal,
        $(($error_name:ident, $code:literal, $body:literal, ($($inner:ty),*))),*
    ) => {
        #[derive(Clone, Debug, Eq, PartialEq)]
        pub enum $name {$(
            $error_name($($inner),*),
        )*}

        impl $name {
            pub fn code(&self) -> usize {
                match self {$(
                    Self::$error_name(..) => $code,
                )*}
            }
            fn padding_len(&self) -> usize {
                match self {$(
                    Self::$error_name(..) => $code_len - num_digits($code),
                )*}
            }
            pub fn message(&self) -> String {
                match self {$(
                    Self::$error_name(..) => format_message!(self, $error_name, $body, $($inner),*),
                )*}
            }
        }

        impl std::fmt::Display for $name {
            fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
                write!(f, concat!("[", $code_pfx, "{}{}] ", $message_pfx, ": {}"), "0".repeat(self.padding_len()), self.code(), self.message())
            }
        }
    };
}

error_messages! { ErrorMessage
    code: "TQL", type: "TypeQL Error",
    SyntaxErrorDetailed(usize, String, String, String) =
        3: "There is a syntax error at line {}:\n{}\n{}\n{}",
    InvalidCasting(&'static str, &'static str, &'static str, &'static str) =
        4: "Enum '{}::{}' does not match '{}', and cannot be unwrapped into '{}'.",
    MissingPatterns() =
        5: "The query has not been provided with any patterns.",
    MissingDefinables() =
        6: "The query has not been provided with any definables.",
    MatchHasNoBoundingNamedVariable() =
        7: "The match query does not have named variables to bound the nested disjunction/negation pattern(s).",
    MatchPatternVariableHasNoNamedVariable(Pattern) =
        9: "The pattern '{}' has no named variable.",
    MatchHasUnboundedNestedPattern(Pattern) =
        10: "The match query contains a nested pattern is not bounded: '{}'.",
    EmptyMatchFilter() =
        12: "The match query cannot be filtered with an empty list of variables.",
    InvalidIIDString(String) =
        13: "Invalid IID: '{}'. IIDs must follow the regular expression: '0x[0-9a-f]+'.",
    InvalidAttributeTypeRegex(String) =
        14: "Invalid regular expression '{}'.",
    IllegalFilterVariableRepeating(Reference) =
        15: "The variable '{}' occurred more than once in match query filter.",
    VariableOutOfScopeMatch(Reference) =
        16: "The variable '{}' is out of scope of the match query.",
    VariableOutOfScopeDelete(Reference) =
        17: "The deleted variable '{}' is out of scope of the match query.",
    NoVariableInScopeInsert(String, String) =
        18: "None of the variables in 'insert' ('{}') is within scope of 'match' ('{}')",
    VariableNotNamed() =
        19: "Anonymous variable encountered in a match query filter.",
    InvalidVariableName(String) =
        20: "The variable name '{}' is invalid; variables must match the following regular expression: '^[a-zA-Z0-9][a-zA-Z0-9_-]+$'.",
    MissingConstraintRelationPlayer() =
        22: "A relation variable has not been provided with role players.",
    InvalidConstraintPredicate(token::Predicate, Value) =
        25: "The '{}' constraint may only accept a string value as its operand, got '{}' instead.",
    InvalidConstraintDatetimePrecision(NaiveDateTime) =
        26: "Attempted to assign DateTime value of '{}' which is more precise than 1 millisecond.",
    InvalidDefineQueryVariable() =
        27: "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.",
    InvalidUndefineQueryRule(Label) =
        28: "Invalid undefine query: the rule body of '{}' ('when' or 'then') cannot be undefined. The rule must be undefined entirely by referring to its label.",
    InvalidRuleWhenMissingPatterns(Label) =
        29: "Rule '{}' 'when' has not been provided with any patterns.",
    InvalidRuleWhenNestedNegation(Label) =
        30: "Rule '{}' 'when' contains a nested negation.",
    InvalidRuleThen(Label, ThingVariable) =
        31: "Rule '{}' 'then' '{}': must be exactly one attribute ownership, or exactly one relation.",
    InvalidRuleThenHas(Label, ThingVariable, Reference, TypeVariable) =
        32: "Rule '{}' 'then' '{}' tries to assign type '{}' to variable '{}', but this variable already had a type assigned by the rule 'when'. Try omitting this type assignment.",
    InvalidRuleThenVariables(Label) =
        33: "Rule '{}' 'then' variables must be present in the 'when', outside of nested patterns.",
    InvalidRuleThenRoles(Label, ThingVariable) =
        34: "Rule '{}' 'then' '{}' must specify all role types explicitly or by using a variable.",
    RedundantNestedNegation() =
        35: "Invalid query containing redundant nested negations.",
    VariableNotSorted(UnboundVariable) =
        36: "Variable '{}' does not exist in the sorting clause.",
    InvalidCountVariableArgument() =
        38: "Aggregate COUNT does not accept a Variable.",
    IllegalGrammar(String) =
        39: "Illegal grammar: '{}'",
    IllegalCharInLabel(String) =
        40: "'{}' is not a valid Type label. Type labels must start with a letter, and may contain only letters, numbers, '-' and '_'.",
}
