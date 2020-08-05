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

package graql.lang.query.builder;

import graql.lang.Graql;
import graql.lang.query.GraqlQuery;
import graql.lang.variable.UnscopedVariable;

public interface Aggregatable<T extends GraqlQuery> {

    default T count() {
        return aggregate(Graql.Token.Aggregate.Method.COUNT, null);
    }

    default T max(String var) {
        return max(UnscopedVariable.named(var));
    }

    default T max(UnscopedVariable var) {
        return aggregate(Graql.Token.Aggregate.Method.MAX, var);
    }

    default T mean(String var) {
        return mean(UnscopedVariable.named(var));
    }

    default T mean(UnscopedVariable var) {
        return aggregate(Graql.Token.Aggregate.Method.MEAN, var);
    }

    default T median(String var) {
        return median(UnscopedVariable.named(var));
    }

    default T median(UnscopedVariable var) {
        return aggregate(Graql.Token.Aggregate.Method.MEDIAN, var);
    }

    default T min(String var) {
        return min(UnscopedVariable.named(var));
    }

    default T min(UnscopedVariable var) {
        return aggregate(Graql.Token.Aggregate.Method.MIN, var);
    }

    default T std(String var) {
        return std(UnscopedVariable.named(var));
    }

    default T std(UnscopedVariable var) {
        return aggregate(Graql.Token.Aggregate.Method.STD, var);
    }

    default T sum(String var) {
        return sum(UnscopedVariable.named(var));
    }

    default T sum(UnscopedVariable var) {
        return aggregate(Graql.Token.Aggregate.Method.SUM, var);
    }

    // TODO: will be made "private" once we upgrade to Java 9 or higher
    T aggregate(Graql.Token.Aggregate.Method method, UnscopedVariable var);
}
