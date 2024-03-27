/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.common.exception;

public class ErrorMessage extends com.vaticle.typedb.common.exception.ErrorMessage {

    public static final ErrorMessage ILLEGAL_STATE =
            new ErrorMessage(1, "Illegal internal state!");
    public static final ErrorMessage SYNTAX_ERROR_NO_DETAILS =
            new ErrorMessage(2, "There is a syntax error at line %s:\n%s");
    public static final ErrorMessage SYNTAX_ERROR_DETAILED =
            new ErrorMessage(3, "There is a syntax error at line %s:\n%s\n%s\n%s");
    public static final ErrorMessage INVALID_CASTING =
            new ErrorMessage(4, "The class '%s' cannot be casted to '%s'.");
    public static final ErrorMessage MISSING_PATTERNS =
            new ErrorMessage(5, "The query has not been provided with any patterns.");
    public static final ErrorMessage VARIABLE_NAME_CONFLICT =
            new ErrorMessage(6, "The variable(s) named '%s' cannot be used for both concept variables and a value variables.");
    public static final ErrorMessage MISSING_DEFINABLES =
            new ErrorMessage(7, "The query has not been provided with any definables.");
    public static final ErrorMessage MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE =
            new ErrorMessage(8, "The match query does not have named variables to bound the nested disjunction/negation pattern(s).");
    public static final ErrorMessage MATCH_HAS_NO_NAMED_VARIABLE =
            new ErrorMessage(9, "The match query has no named variables to retrieve.");
    public static final ErrorMessage MATCH_PATTERN_STATEMENT_HAS_NO_NAMED_VARIABLE =
            new ErrorMessage(10, "The pattern '%s' has no named variable.");
    public static final ErrorMessage MATCH_HAS_UNBOUNDED_NESTED_PATTERN =
            new ErrorMessage(11, "The match query contains a nested pattern is not bounded: '%s'.");
    public static final ErrorMessage MISSING_GET_FILTER =
            new ErrorMessage(12, "The get query cannot be constructed with NULL filter variable collection.");
    public static final ErrorMessage INVALID_IID_STRING =
            new ErrorMessage(13, "Invalid IID: '%s'. IIDs must follow the regular expression: '%s'.");
    public static final ErrorMessage INVALID_ATTRIBUTE_TYPE_REGEX =
            new ErrorMessage(14, "Invalid regular expression '%s'.");
    public static final ErrorMessage ILLEGAL_FILTER_VARIABLE_REPEATING =
            new ErrorMessage(15, "The variable '%s' occurred more than once in the filter.");
    public static final ErrorMessage VARIABLE_OUT_OF_SCOPE =
            new ErrorMessage(16, "The variable '%s' is out of scope of the query.");
    public static final ErrorMessage VARIABLE_OUT_OF_SCOPE_DELETE =
            new ErrorMessage(17, "The deleted variable '%s' is out of scope of the match query.");
    public static final ErrorMessage NO_VARIABLE_IN_SCOPE_INSERT =
            new ErrorMessage(18, "None of the variables in 'insert' ('%s') is within scope of 'match' ('%s')");
    public static final ErrorMessage FILTER_VARIABLE_ANONYMOUS =
            new ErrorMessage(19, "Anonymous variable encountered in the query filter.");
    public static final ErrorMessage INVALID_VARIABLE_NAME =
            new ErrorMessage(20, "Invalid variable name '%s'. Variables must be valid unicode identifiers.");
    public static final ErrorMessage ILLEGAL_CONSTRAINT_REPETITION =
            new ErrorMessage(21, "The variable '%s' contains illegally repeating constraints: '%s' and '%s'.");
    public static final ErrorMessage MISSING_CONSTRAINT_RELATION_PLAYER =
            new ErrorMessage(22, "A relation variable has not been provided with role players.");
    public static final ErrorMessage MISSING_CONSTRAINT_VALUE =
            new ErrorMessage(23, "A value constraint has not been provided with a variable or literal value.");
    public static final ErrorMessage MISSING_CONSTRAINT_PREDICATE =
            new ErrorMessage(24, "A value constraint has not been provided with a predicate.");
    public static final ErrorMessage INVALID_CONSTRAINT_DATETIME_PRECISION =
            new ErrorMessage(26, "Attempted to assign DateTime value of '%s' which is more precise than 1 millisecond.");
    public static final ErrorMessage INVALID_DEFINE_QUERY_VARIABLE =
            new ErrorMessage(27, "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.");
    public static final ErrorMessage INVALID_UNDEFINE_QUERY_RULE =
            new ErrorMessage(28, "Invalid undefine query: the rule body of '%s' ('when' or 'then') cannot be undefined. The rule must be undefined entirely by referring to its label.");
    public static final ErrorMessage INVALID_RULE_WHEN_MISSING_PATTERNS =
            new ErrorMessage(29, "Rule '%s' 'when' has not been provided with any patterns.");
    public static final ErrorMessage INVALID_RULE_WHEN_NESTED_NEGATION =
            new ErrorMessage(30, "Rule '%s' 'when' contains a nested negation.");
    public static final ErrorMessage INVALID_RULE_THEN =
            new ErrorMessage(31, "Rule '%s' 'then' '%s': must be exactly one attribute ownership, or exactly one relation.");
    public static final ErrorMessage INVALID_RULE_THEN_HAS =
            new ErrorMessage(32, "Rule '%s' 'then' '%s' tries to assign type '%s' to variable '%s', but this variable already had a type assigned by the rule 'when'. Try omitting this type assignment.");
    public static final ErrorMessage INVALID_RULE_THEN_VARIABLES =
            new ErrorMessage(33, "Rule '%s' 'then' variables must be present in the 'when', outside of nested patterns.");
    public static final ErrorMessage INVALID_RULE_THEN_ROLES =
            new ErrorMessage(34, "Rule '%s' 'then' '%s' must specify all role types explicitly or by using a variable.");
    public static final ErrorMessage INVALID_RULE_THEN_RELATION_VARIABLE =
            new ErrorMessage(35, "Rule '%s': relation variable name '%s' in 'then' must not be present.");
    public static final ErrorMessage REDUNDANT_NESTED_NEGATION =
            new ErrorMessage(36, "Invalid query containing redundant nested negations.");
    public static final ErrorMessage VARIABLE_NOT_SORTED =
            new ErrorMessage(37, "Variable '%s' does not exist in the sorting clause.");
    public static final ErrorMessage INVALID_SORTING_VARIABLE_NOT_MATCHED =
            new ErrorMessage(38, "Sort variable '%s' is not present in the match clause.");
    public static final ErrorMessage INVALID_SORTING_ORDER =
            new ErrorMessage(39, "Invalid sorting order '%s'. Valid options: '%s' or '%s'.");
    public static final ErrorMessage INVALID_COUNT_VARIABLE_ARGUMENT =
            new ErrorMessage(40, "Aggregate COUNT does not accept a Variable.");
    public static final ErrorMessage ILLEGAL_GRAMMAR =
            new ErrorMessage(41, "Illegal grammar: '%s'");
    public static final ErrorMessage INVALID_TYPE_LABEL =
            new ErrorMessage(42, "The type label '%s' is invalid. Type labels must be valid unicode identifiers.");
    public static final ErrorMessage INVALID_ANNOTATION =
            new ErrorMessage(43, "Invalid annotation '%s' on '%s' constraint");

    private static final String codePrefix = "TQL";
    private static final String messagePrefix = "TypeQL Error";

    public ErrorMessage(int codeNumber, String messageBody) {
        super(codePrefix, codeNumber, messagePrefix, messageBody);
    }
}
