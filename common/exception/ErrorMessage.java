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
    public static final ErrorMessage MISSING_DEFINABLES =
            new ErrorMessage(6, "The query has not been provided with any definables.");
    public static final ErrorMessage MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE =
            new ErrorMessage(7, "The match query does not have named variables to bound the nested disjunction/negation pattern(s).");
    public static final ErrorMessage MATCH_HAS_NO_NAMED_VARIABLE =
            new ErrorMessage(8, "The match query has no named variables to retrieve.");
    public static final ErrorMessage MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE =
            new ErrorMessage(9, "The pattern '%s' has no named variable.");
    public static final ErrorMessage MATCH_HAS_UNBOUNDED_NESTED_PATTERN =
            new ErrorMessage(10, "The match query contains a nested pattern is not bounded: '%s'.");
    public static final ErrorMessage MISSING_MATCH_FILTER =
            new ErrorMessage(11, "The match query cannot be constructed with NULL filter variable collection.");
    public static final ErrorMessage EMPTY_MATCH_FILTER =
            new ErrorMessage(12, "The match query cannot be filtered with an empty list of variables.");
    public static final ErrorMessage INVALID_IID_STRING =
            new ErrorMessage(13, "Invalid IID: '%s'. IIDs must follow the regular expression: '%s'.");
    public static final ErrorMessage INVALID_ATTRIBUTE_TYPE_REGEX =
            new ErrorMessage(14, "Invalid regular expression '%s'.");
    public static final ErrorMessage ILLEGAL_FILTER_VARIABLE_REPEATING =
            new ErrorMessage(15, "The variable '%s' occurred more than once in match query filter.");
    public static final ErrorMessage VARIABLE_OUT_OF_SCOPE_MATCH =
            new ErrorMessage(16, "The variable '%s' is out of scope of the match query.");
    public static final ErrorMessage VARIABLE_OUT_OF_SCOPE_DELETE =
            new ErrorMessage(17, "The deleted variable '%s' is out of scope of the match query.");
    public static final ErrorMessage NO_VARIABLE_IN_SCOPE_INSERT =
            new ErrorMessage(18, "None of the variables in 'insert' ('%s') is within scope of 'match' ('%s')");
    public static final ErrorMessage VARIABLE_NOT_NAMED =
            new ErrorMessage(19, "The variable '%s' is not named and cannot be used as a filter for match query.");
    public static final ErrorMessage INVALID_VARIABLE_NAME =
            new ErrorMessage(20, "The variable name '%s' is invalid; variables must match the following regular expression: '%s'.");
    public static final ErrorMessage ILLEGAL_CONSTRAINT_REPETITION =
            new ErrorMessage(21, "The variable '%s' contains illegally repeating constraints: '%s' and '%s'.");
    public static final ErrorMessage MISSING_CONSTRAINT_RELATION_PLAYER =
            new ErrorMessage(22, "A relation variable has not been provided with role players.");
    public static final ErrorMessage MISSING_CONSTRAINT_VALUE =
            new ErrorMessage(23, "A value constraint has not been provided with a variable or literal value.");
    public static final ErrorMessage MISSING_CONSTRAINT_PREDICATE =
            new ErrorMessage(24, "A value constraint has not been provided with a predicate.");
    public static final ErrorMessage INVALID_CONSTRAINT_DATETIME_PRECISION =
            new ErrorMessage(25, "Attempted to assign DateTime value of '%s' which is more precise than 1 millisecond.");
    public static final ErrorMessage INVALID_DEFINE_QUERY_VARIABLE =
            new ErrorMessage(26, "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.");
    public static final ErrorMessage INVALID_RULE_WHEN_MISSING_PATTERNS =
            new ErrorMessage(27, "Rule '%s' 'when' has not been provided with any patterns.");
    public static final ErrorMessage INVALID_RULE_WHEN_NESTED_NEGATION =
            new ErrorMessage(28, "Rule '%s' 'when' contains a nested negation.");
    public static final ErrorMessage INVALID_RULE_WHEN_CONTAINS_DISJUNCTION =
            new ErrorMessage(29, "Rule '%s' 'when' contains a disjunction.");
    public static final ErrorMessage INVALID_RULE_THEN =
            new ErrorMessage(30, "Rule '%s' 'then' '%s': must be exactly one attribute ownership, or exactly one relation.");
    public static final ErrorMessage INVALID_RULE_THEN_HAS =
            new ErrorMessage(31, "Rule '%s' 'then' '%s' tries to assign type '%s' to variable '%s', but this variable already had a type assigned by the rule 'when'. Try omitting this type assignment.");
    public static final ErrorMessage INVALID_RULE_THEN_VARIABLES =
            new ErrorMessage(32, "Rule '%s' 'then' variables must be present in rule 'when'.");
    public static final ErrorMessage REDUNDANT_NESTED_NEGATION =
            new ErrorMessage(33, "Invalid query containing redundant nested negations.");
    public static final ErrorMessage INVALID_SORTING_ORDER =
            new ErrorMessage(34, "Invalid sorting order. Valid options: '%s' or '%s'.");
    public static final ErrorMessage INVALID_COUNT_VARIABLE_ARGUMENT =
            new ErrorMessage(35, "Aggregate COUNT does not accept a Variable.");
    public static final ErrorMessage ILLEGAL_GRAMMAR =
            new ErrorMessage(36, "Illegal grammar!");
    public static final ErrorMessage ILLEGAL_CHAR_IN_LABEL =
            new ErrorMessage(47, "'%s' is not a valid Type label. Type labels must start with a letter, and may contain only letters, numbers, '-' and '_'.");


    private static final String codePrefix = "TQL";
    private static final String messagePrefix = "TypeQL Error";

    public ErrorMessage(int codeNumber, String messageBody) {
        super(codePrefix, codeNumber, messagePrefix, messageBody);
    }
}
