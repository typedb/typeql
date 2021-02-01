/*
 * Copyright (C) 2021 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package graql.lang.common.exception;

public class ErrorMessage extends grakn.common.exception.ErrorMessage {

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
    public static final ErrorMessage MISSING_DEFINABLES =
            new ErrorMessage(6, "The query has not been provided with any definables.");
    public static final ErrorMessage MISSING_FILTER_VARIABLES =
            new ErrorMessage(7, "The match query has not been provided with any filter variables.");
    public static final ErrorMessage INVALID_IID_STRING =
            new ErrorMessage(8, "Invalid IID: '%s'. IIDs must follow the regular expression: '%s'.");
    public static final ErrorMessage INVALID_ATTRIBUTE_TYPE_REGEX =
            new ErrorMessage(9, "Invalid regular expression '%s'.");
    public static final ErrorMessage ILLEGAL_FILTER_VARIABLE_REPEATING =
            new ErrorMessage(10, "The variable '%s' occurred more than once in match query filter.");
    public static final ErrorMessage VARIABLE_OUT_OF_SCOPE_MATCH =
            new ErrorMessage(11, "The variable '%s' is out of scope of the match query.");
    public static final ErrorMessage VARIABLE_OUT_OF_SCOPE_DELETE =
            new ErrorMessage(12, "The deleted variable '%s' is out of scope of the match query.");
    public static final ErrorMessage NO_VARIABLE_IN_SCOPE_INSERT =
            new ErrorMessage(13, "None of the variables in 'insert' ('%s') is within scope of 'match' ('%s')");
    public static final ErrorMessage VARIABLE_NOT_NAMED =
            new ErrorMessage(14, "The variable '%s' is not named and cannot be used as a filter for match query.");
    public static final ErrorMessage INVALID_VARIABLE_NAME =
            new ErrorMessage(15, "The variable name '%s' is invalid; variables must match the following regular expression: '%s'.");
    public static final ErrorMessage ILLEGAL_CONSTRAINT_REPETITION =
            new ErrorMessage(16, "The variable '%s' contains illegally repeating constraints: '%s' and '%s'.");
    public static final ErrorMessage MISSING_CONSTRAINT_RELATION_PLAYER =
            new ErrorMessage(17, "A relation variable has not been provided with role players.");
    public static final ErrorMessage MISSING_CONSTRAINT_VALUE =
            new ErrorMessage(18, "A value constraint has not been provided with a variable or literal value.");
    public static final ErrorMessage MISSING_CONSTRAINT_PREDICATE =
            new ErrorMessage(19, "A value constraint has not been provided with a predicate.");
    public static final ErrorMessage INVALID_CONSTRAINT_DATETIME_PRECISION =
            new ErrorMessage(20, "Attempted to assign DateTime value of '%s' which is more precise than 1 millisecond.");
    public static final ErrorMessage INVALID_DEFINE_QUERY_VARIABLE =
            new ErrorMessage(21, "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.");
    public static final ErrorMessage INVALID_RULE_WHEN_MISSING_PATTERNS =
            new ErrorMessage(22, "Rule '%s' 'when' has not been provided with any patterns.");
    public static final ErrorMessage INVALID_RULE_WHEN_NESTED_NEGATION =
            new ErrorMessage(23, "Rule '%s' 'when' contains a nested negation.");
    public static final ErrorMessage INVALID_RULE_WHEN_CONTAINS_DISJUNCTION =
            new ErrorMessage(24, "Rule '%s' 'when' contains a disjunction.");
    public static final ErrorMessage INVALID_RULE_THEN =
            new ErrorMessage(25, "Rule '%s' 'then' '%s': must be exactly one attribute ownership, or exactly one relation.");
    public static final ErrorMessage INVALID_RULE_THEN_HAS =
            new ErrorMessage(26, "Rule '%s' 'then' '%s': is trying to assign both an attribute type and a variable attribute value.");
    public static final ErrorMessage INVALID_RULE_THEN_VARIABLES =
            new ErrorMessage(27, "Rule '%s' 'then' variables must be present in rule 'when'.");
    public static final ErrorMessage REDUNDANT_NESTED_NEGATION =
            new ErrorMessage(28, "Invalid query containing redundant nested negations.");
    public static final ErrorMessage MISSING_COMPUTE_CONDITION =
            new ErrorMessage(29, "Missing condition(s) for 'compute '%s''. The required condition(s) are: '%s'.");
    public static final ErrorMessage INVALID_COMPUTE_METHOD_ALGORITHM =
            new ErrorMessage(30, "Invalid algorithm for 'compute '%s''. The accepted algorithm(s) are: '%s'.");
    public static final ErrorMessage INVALID_COMPUTE_ARGUMENT =
            new ErrorMessage(31, "Invalid argument(s) 'compute %s using %s'. The accepted argument(s) are: '%s'.");
    public static final ErrorMessage INVALID_SORTING_ORDER =
            new ErrorMessage(32, "Invalid sorting order. Valid options: '%s' or '%s'.");
    public static final ErrorMessage INVALID_COUNT_VARIABLE_ARGUMENT =
            new ErrorMessage(33, "Aggregate COUNT does not accept a Variable.");
    public static final ErrorMessage ILLEGAL_GRAMMAR =
            new ErrorMessage(34, "Illegal grammar!");


    private static final String codePrefix = "GQL";
    private static final String messagePrefix = "Graql Error";

    public ErrorMessage(int codeNumber, String messageBody) {
        super(codePrefix, codeNumber, messagePrefix, messageBody);
    }
}
