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

#[macro_use]
mod macros;

use std::{error::Error as StdError, fmt};

use chrono::NaiveDateTime;
use pest::error::{Error as PestError, LineColLocation};

use crate::{
    common::token,
    error_messages,
    pattern::{Label, Pattern, Reference, ThingVariable, TypeVariable, UnboundConceptVariable, UnboundVariable, Value},
    write_joined,
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct Error {
    errors: Vec<TypeQLError>,
}

impl StdError for Error {}

impl From<TypeQLError> for Error {
    fn from(error: TypeQLError) -> Self {
        Self { errors: vec![error] }
    }
}

impl From<Vec<TypeQLError>> for Error {
    fn from(errors: Vec<TypeQLError>) -> Self {
        assert!(!errors.is_empty());
        Self { errors }
    }
}

impl<T: pest::RuleType> From<PestError<T>> for Error {
    fn from(error: PestError<T>) -> Self {
        let (line, col) = match error.line_col {
            LineColLocation::Pos((line, col)) => (line, col),
            LineColLocation::Span((line, col), _) => (line, col),
        };
        Self::from(TypeQLError::SyntaxErrorDetailed(
            line,
            error.line().to_owned(),
            " ".repeat(col - 1) + "^",
            error.variant.message().to_string(),
        ))
    }
}

impl fmt::Display for Error {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write_joined!(f, "\n\n", self.errors)
    }
}

pub fn collect_err(i: &mut dyn Iterator<Item = Result<(), Error>>) -> Result<(), Error> {
    let errors = i.filter_map(Result::err).flat_map(|e| e.errors).collect::<Vec<_>>();
    if errors.is_empty() {
        Ok(())
    } else {
        Err(Error { errors })
    }
}

error_messages! { TypeQLError
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
