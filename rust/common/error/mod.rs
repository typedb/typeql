/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{error::Error as StdError, fmt};

use chrono::NaiveDateTime;
use itertools::Itertools;
use pest::error::{Error as PestError, LineColLocation};

use crate::{
    common::token,
    error_messages,
    pattern::{Label, Pattern, ThingStatement, Value},
    write_joined,
};
use crate::variable::Variable;

#[macro_use]
mod macros;

const SYNTAX_ERROR_INDENT: usize = 4;
const SYNTAX_ERROR_INDICATOR: &str = "--> ";

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

pub(crate) fn syntax_error<T: pest::RuleType>(query: &str, error: PestError<T>) -> TypeQLError {
    let (error_line_nr, _) = match error.line_col {
        LineColLocation::Pos((line, col)) => (line, col),
        LineColLocation::Span((line, col), _) => (line, col),
    };
    // error_line_nr is 1-indexed, we operate on 0-offset
    let error_line = error_line_nr - 1;
    let formatted_error = query
        .lines()
        .enumerate()
        .map(|(i, line)| {
            if i == error_line {
                format!("{SYNTAX_ERROR_INDICATOR}{line}")
            } else {
                format!("{}{line}", " ".repeat(SYNTAX_ERROR_INDENT))
            }
        })
        .join("\n");
    TypeQLError::SyntaxErrorDetailed { error_line_nr, formatted_error }
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
    SyntaxErrorDetailed { error_line_nr: usize, formatted_error: String } =
        3: "There is a syntax error near line {error_line_nr}:\n{formatted_error}",
    InvalidCasting { enum_name: &'static str, variant: &'static str, expected_variant: &'static str, typename: &'static str } =
        4: "Enum '{enum_name}::{variant}' does not match '{expected_variant}', and cannot be unwrapped into '{typename}'.",
    MissingPatterns =
        5: "The query has not been provided with any patterns.",
    MissingDefinables =
        6: "The query has not been provided with any definables.",
    MatchHasNoBoundingNamedVariable =
        7: "The match query does not have named variables to bound the nested disjunction/negation pattern(s).",
    VariableNameConflict { names: String } =
        8: "The variable names {names} cannot be used for both concept variables and value variables.",
    MatchStatementHasNoNamedVariable { pattern: Pattern } =
        9: "The statement '{pattern}' has no named variable.",
    MatchHasUnboundedNestedPattern { pattern: Pattern } =
        10: "The match query contains a nested pattern is not bounded: '{pattern}'.",
    InvalidIIDString { iid: String } =
        11: "Invalid IID: '{iid}'. IIDs must follow the regular expression: '0x[0-9a-f]+'.",
    InvalidAttributeTypeRegex { regex: String } =
        12: "Invalid regular expression '{regex}'.",
    GetVarRepeating { variable: Variable } =
        13: "The variable '{variable}' occurred more than once in get query filter.",
    GetVarNotBound { variable: Variable } =
        14: "The get variable '{variable}' is not bound in the match clause.",
    AggregateVarNotBound { variable: Variable } =
        15: "The get-aggregate variable '{variable}' is not bound in the match clause.",
    GroupVarNotBound { variable: Variable } =
        16: "The get-group variable '{variable}' is not bound in the match clause.",
    SortVarNotBound { variable: Variable } =
        17: "The sort variable '{variable}' is not bound in the match clause.",
    DeleteVarNotBound { variable: Variable } =
        18: "The delete variable '{variable}' is not bound in the match clause.",
    InsertClauseNotBound { insert_statements: String, bounds: String } =
        19: "None of the variables in 'insert' ('{insert_statements}') is within scope of 'match' ('{bounds}')",
    InsertModifiersRequireMatch { insert: String } =
        20: "The insert query '{insert}' contains query modifiers that require a 'match' clause be specified",
    VariableNotNamed =
        21: "Anonymous variable encountered in a match query filter.",
    InvalidVariableName { name: String } =
        22: "The variable name '{name}' is invalid. Variables must be valid utf-8 identifiers without a leading underscore.",
    MissingConstraintRelationPlayer =
        23: "A relation variable has not been provided with role players.",
    InvalidConstraintPredicate { predicate: token::Comparator, value: Value } =
        24: "The '{predicate}' constraint may only accept a string value as its operand, got '{value}' instead.",
    InvalidConstraintDatetimePrecision { date_time: NaiveDateTime } =
        25: "Attempted to assign DateTime value of '{date_time}' which is more precise than 1 millisecond.",
    InvalidDefineQueryVariable =
        26: "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.",
    InvalidUndefineQueryRule { rule_label: Label } =
        27: "Invalid undefine query: the rule body of '{rule_label}' ('when' or 'then') cannot be undefined. The rule must be undefined entirely by referring to its label.",
    InvalidRuleWhenMissingPatterns { rule_label: Label } =
        28: "Rule '{rule_label}' 'when' has not been provided with any patterns.",
    InvalidRuleWhenNestedNegation { rule_label: Label } =
        29: "Rule '{rule_label}' 'when' contains a nested negation.",
    InvalidRuleThen { rule_label: Label, then: ThingStatement } =
        30: "Rule '{rule_label}' 'then' '{then}': must be exactly one attribute ownership, or exactly one relation.",
    InvalidRuleThenHas { rule_label: Label, then: ThingStatement, variable: Variable, type_label: Label } =
        31: "Rule '{rule_label}' 'then' '{then}' tries to assign type '{type_label}' to variable '{variable}', but this variable already had a type assigned by the rule 'when'. Try omitting this type assignment.",
    InvalidRuleThenVariables { rule_label: Label } =
        32: "Rule '{rule_label}' 'then' variables must be present in the 'when', outside of nested patterns.",
    InvalidRuleThenRoles { rule_label: Label, then: ThingStatement } =
        33: "Rule '{rule_label}' 'then' '{then}' must specify all role types explicitly or by using a variable.",
    RedundantNestedNegation =
        34: "Invalid query containing redundant nested negations.",
    VariableNotSorted { variable: Variable } =
        35: "Variable '{variable}' does not exist in the sorting clause.",
    InvalidCountVariableArgument =
        36: "Aggregate COUNT does not accept a Variable.",
    IllegalGrammar { input: String } =
        37: "Illegal grammar: '{input}'",
    InvalidTypeLabel { label: String } =
        38: "The type label '{label}' is invalid. Type labels must be valid utf-8 identifiers without a leading underscore.",
}
