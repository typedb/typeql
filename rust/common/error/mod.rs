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

use std::{error::Error as StdError, fmt};

use chrono::NaiveDateTime;
use pest::error::{Error as PestError, LineColLocation};

use crate::{
    common::token,
    error_messages,
    pattern::{Label, Pattern, ThingStatement, Value},
    variable::Variable,
    write_joined,
};
use crate::variable::ConceptVariable;

#[macro_use]
mod macros;

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

pub fn collect_err(i: impl IntoIterator<Item = Result<(), Error>>) -> Result<(), Error> {
    let errors = i.into_iter().filter_map(Result::err).flat_map(|e| e.errors).collect::<Vec<_>>();
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
    VariableNameConflict(String) =
        8: "The variable names '{}' cannot be used for both concept variables and value variables.",
    MatchStatementHasNoNamedVariable(Pattern) =
        9: "The statement '{}' has no named variable.",
    MatchHasUnboundedNestedPattern(Pattern) =
        10: "The match query contains a nested pattern is not bounded: '{}'.",
    InvalidIIDString(String) =
        11: "Invalid IID: '{}'. IIDs must follow the regular expression: '0x[0-9a-f]+'.",
    InvalidAttributeTypeRegex(String) =
        12: "Invalid regular expression '{}'.",
    GetVarRepeating(Variable) =
        13: "The variable '{}' occurred more than once in get query filter.",
    GetVarNotBound(Variable) =
        14: "The get variable '{}' is not bound in the match clause.",
    SortVarNotAvailable(Variable) =
        15: "The sort variable '{}' is not available from the preceding match clause.",
    VariableOutOfScopeDelete(Variable) =
        16: "The delete variable '{}' is out of scope of the match query.",
    NoVariableInScopeInsert(String, String) =
        17: "None of the variables in 'insert' ('{}') is within scope of 'match' ('{}')",
    VariableNotNamed() =
        18: "Anonymous variable encountered in a match query filter.",
    InvalidVariableName(String) =
        19: "The variable name '{}' is invalid; variables must match the following regular expression: '^[a-zA-Z0-9][a-zA-Z0-9_-]+$'.",
    MissingConstraintRelationPlayer() =
        20: "A relation variable has not been provided with role players.",
    InvalidConstraintPredicate(token::Predicate, Value) =
        21: "The '{}' constraint may only accept a string value as its operand, got '{}' instead.",
    InvalidConstraintDatetimePrecision(NaiveDateTime) =
        22: "Attempted to assign DateTime value of '{}' which is more precise than 1 millisecond.",
    InvalidDefineQueryVariable() =
        23: "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.",
    InvalidUndefineQueryRule(Label) =
        24: "Invalid undefine query: the rule body of '{}' ('when' or 'then') cannot be undefined. The rule must be undefined entirely by referring to its label.",
    InvalidRuleWhenMissingPatterns(Label) =
        25: "Rule '{}' 'when' has not been provided with any patterns.",
    InvalidRuleWhenNestedNegation(Label) =
        26: "Rule '{}' 'when' contains a nested negation.",
    InvalidRuleThen(Label, ThingStatement) =
        27: "Rule '{}' 'then' '{}': must be exactly one attribute ownership, or exactly one relation.",
    InvalidRuleThenHas(Label, ThingStatement, ConceptVariable, Label) =
        28: "Rule '{}' 'then' '{}' tries to assign type '{}' to variable '{}', but this variable already had a type assigned by the rule 'when'. Try omitting this type assignment.",
    InvalidRuleThenVariables(Label) =
        29: "Rule '{}' 'then' variables must be present in the 'when', outside of nested patterns.",
    InvalidRuleThenRoles(Label, ThingStatement) =
        30: "Rule '{}' 'then' '{}' must specify all role types explicitly or by using a variable.",
    RedundantNestedNegation() =
        31: "Invalid query containing redundant nested negations.",
    VariableNotSorted(Variable) =
        32: "Variable '{}' does not exist in the sorting clause.",
    InvalidCountVariableArgument() =
        33: "Aggregate COUNT does not accept a Variable.",
    IllegalGrammar(String) =
        34: "Illegal grammar: '{}'",
    IllegalCharInLabel(String) =
        35: "'{}' is not a valid Type label. Type labels must start with a letter, and may contain only letters, numbers, '-' and '_'.",
}
