/*
 * Copyright (C) 2020 Grakn Labs
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

    public static final ErrorMessage SYNTAX_ERROR_NO_DETAILS =
            new ErrorMessage(1, "There is a syntax error at line %s:\n%s");
    public static final ErrorMessage SYNTAX_ERROR_DETAILED =
            new ErrorMessage(2, "There is a syntax error at line %s:\n%s\n%s\n%s");
    public static final ErrorMessage INVALID_CASTING =
            new ErrorMessage(3, "The class '%s' cannot be casted to '%s'.");
    public static final ErrorMessage MISSING_PATTERNS =
            new ErrorMessage(4, "The query has not been provided with any patterns.");
    public static final ErrorMessage MISSING_FILTER_VARIABLES =
            new ErrorMessage(5, "The match query has not been provided with any filter variables.");
    public static final ErrorMessage INVALID_IID_STRING =
            new ErrorMessage(6, "Invalid IID: '%s'. IIDs must follow the regular expression: '%s'.");
    public static final ErrorMessage INVALID_ATTRIBUTE_TYPE_REGEX =
            new ErrorMessage(7, "Invalid regular expression '%s'.");
    public static final ErrorMessage INVALID_VARIABLE_OUT_OF_SCOPE =
            new ErrorMessage(8, "The variable '%s' is out of scope of the query.");
    public static final ErrorMessage INVALID_VARIABLE_NAME =
            new ErrorMessage(9, "The variable name '%s' is invalid; variables must match the following regular expression: '%s'.");
    public static final ErrorMessage ILLEGAL_CONSTRAINT_REPETITION =
            new ErrorMessage(10, "The variable '%s' contains illegally repeating constraints: '%s' and '%s'.");
    public static final ErrorMessage MISSING_CONSTRAINT_RELATION_PLAYER =
            new ErrorMessage(11, "The relation variable has not been provided with role players.");
    public static final ErrorMessage INVALID_CONSTRAINT_DATETIME_PRECISION =
            new ErrorMessage(12, "Attempted to assign DateTime value of '%s' which is more precise than 1 millisecond.");
    public static final ErrorMessage INVALID_DEFINE_QUERY_VARIABLE =
            new ErrorMessage(13, "Invalid define/undefine query. User defined variables are not accepted in define/undefine query.");
    public static final ErrorMessage MISSING_COMPUTE_CONDITION =
            new ErrorMessage(14, "Missing condition(s) for 'compute '%s''. The required condition(s) are: '%s'.");
    public static final ErrorMessage INVALID_COMPUTE_METHOD_ALGORITHM =
            new ErrorMessage(15, "Invalid algorithm for 'compute '%s''. The accepted algorithm(s) are: '%s'.");
    public static final ErrorMessage INVALID_COMPUTE_ARGUMENT =
            new ErrorMessage(16, "Invalid argument(s) 'compute %s using %s'. The accepted argument(s) are: '%s'.");
    public static final ErrorMessage INVALID_CONVERT_OPERATION =
            new ErrorMessage(17, "Invalid 'convert' operation. Only bound and unmerged variables can be converted.");

    private static final String codePrefix = "GQL";
    private static final String messagePrefix = "Graql Error";

    private ErrorMessage(int codeNumber, String messageBody) {
        super(codePrefix, codeNumber, messagePrefix, messageBody);
    }
}
