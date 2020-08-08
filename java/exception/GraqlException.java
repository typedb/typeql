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

package graql.lang.exception;


import graql.lang.Graql;
import graql.lang.query.builder.Filterable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static graql.lang.exception.ErrorMessage.ILLEGAL_REPETITION;
import static graql.lang.exception.ErrorMessage.INVALID_CAST_EXCEPTION;
import static graql.lang.exception.ErrorMessage.INVALID_COMPUTE_ARGUMENT;
import static graql.lang.exception.ErrorMessage.INVALID_COMPUTE_CONDITION;
import static graql.lang.exception.ErrorMessage.INVALID_COMPUTE_METHOD;
import static graql.lang.exception.ErrorMessage.INVALID_COMPUTE_METHOD_ALGORITHM;
import static graql.lang.exception.ErrorMessage.INVALID_VARIABLE_NAME;
import static graql.lang.exception.ErrorMessage.MISSING_COMPUTE_CONDITION;
import static graql.lang.exception.ErrorMessage.OVERPRECISE_SECOND_FRACTION;
import static graql.lang.exception.ErrorMessage.SORTING_NOT_ALLOWED;
import static graql.lang.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE;

// TODO: Refactor this to the style we have in Grakn 2.0
public class GraqlException extends RuntimeException {

    protected GraqlException(String error) {
        super(error);
    }

    protected GraqlException(String error, Exception e) {
        super(error, e);
    }

    public String getName() {
        return this.getClass().getName();
    }

    public static GraqlException create(String error) {
        return new GraqlException(error);
    }

    public static GraqlException illegalRepetitions(String variable, String property1, String property2) {
        return new GraqlException(ILLEGAL_REPETITION.getMessage(variable, property1, property2));
    }

    public static GraqlException variableOutOfScope(String var) {
        return new GraqlException(VARIABLE_OUT_OF_SCOPE.getMessage(var));
    }

    public static GraqlException noPatterns() {
        return new GraqlException(ErrorMessage.NO_PATTERNS.getMessage());
    }

    public static GraqlException invalidCastException(Class<?> origin, Class<?> target) {
        return new GraqlException(INVALID_CAST_EXCEPTION.getMessage(origin.getSimpleName(), target.getSimpleName()));
    }

    public static GraqlException invalidVariableName(String name, String regex) {
        return new GraqlException(INVALID_VARIABLE_NAME.getMessage(name, regex));
    }

    public static GraqlException invalidComputeQuery_invalidMethod(List<Graql.Token.Compute.Method> methods) {
        return new GraqlException(INVALID_COMPUTE_METHOD.getMessage(methods));
    }

    public static GraqlException invalidComputeQuery_invalidCondition(Graql.Token.Compute.Method method, Set<Graql.Token.Compute.Condition> accepted) {
        return new GraqlException(INVALID_COMPUTE_CONDITION.getMessage(method, accepted));
    }

    public static GraqlException invalidComputeQuery_missingCondition(Graql.Token.Compute.Method method, Set<Graql.Token.Compute.Condition> required) {
        return new GraqlException(MISSING_COMPUTE_CONDITION.getMessage(method, required));
    }

    public static GraqlException invalidComputeQuery_invalidMethodAlgorithm(Graql.Token.Compute.Method method, Set<Graql.Token.Compute.Algorithm> accepted) {
        return new GraqlException(INVALID_COMPUTE_METHOD_ALGORITHM.getMessage(method, accepted));
    }

    public static GraqlException invalidComputeQuery_invalidArgument(Graql.Token.Compute.Method method, Graql.Token.Compute.Algorithm algorithm, Set<Graql.Token.Compute.Param> accepted) {
        return new GraqlException(INVALID_COMPUTE_ARGUMENT.getMessage(method, algorithm, accepted));
    }

    public static GraqlException subsecondPrecisionTooPrecise(LocalDateTime localDateTime) {
        return new GraqlException(OVERPRECISE_SECOND_FRACTION.getMessage(localDateTime));
    }

    public static GraqlException sortingNotAllowed(Filterable.Sorting sorting) {
        return new GraqlException(SORTING_NOT_ALLOWED.getMessage(sorting));
    }
}