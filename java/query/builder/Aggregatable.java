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

import graql.lang.common.GraqlToken;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.query.GraqlQuery;

public interface Aggregatable<T extends GraqlQuery> {

    default T count() {
        return aggregate(GraqlToken.Aggregate.Method.COUNT, null);
    }

    default T max(String var) {
        return max(UnboundVariable.named(var));
    }

    default T max(UnboundVariable var) {
        return aggregate(GraqlToken.Aggregate.Method.MAX, var);
    }

    default T mean(String var) {
        return mean(UnboundVariable.named(var));
    }

    default T mean(UnboundVariable var) {
        return aggregate(GraqlToken.Aggregate.Method.MEAN, var);
    }

    default T median(String var) {
        return median(UnboundVariable.named(var));
    }

    default T median(UnboundVariable var) {
        return aggregate(GraqlToken.Aggregate.Method.MEDIAN, var);
    }

    default T min(String var) {
        return min(UnboundVariable.named(var));
    }

    default T min(UnboundVariable var) {
        return aggregate(GraqlToken.Aggregate.Method.MIN, var);
    }

    default T std(String var) {
        return std(UnboundVariable.named(var));
    }

    default T std(UnboundVariable var) {
        return aggregate(GraqlToken.Aggregate.Method.STD, var);
    }

    default T sum(String var) {
        return sum(UnboundVariable.named(var));
    }

    default T sum(UnboundVariable var) {
        return aggregate(GraqlToken.Aggregate.Method.SUM, var);
    }

    T aggregate(GraqlToken.Aggregate.Method method, UnboundVariable var);
}
