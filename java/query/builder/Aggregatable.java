/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.query.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.TypeQLQuery;

public interface Aggregatable<T extends TypeQLQuery> {

    default T count() {
        return aggregate(TypeQLToken.Aggregate.Method.COUNT, null);
    }

    default T max(String var) {
        return max(UnboundVariable.named(var));
    }

    default T max(UnboundVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MAX, var);
    }

    default T mean(String var) {
        return mean(UnboundVariable.named(var));
    }

    default T mean(UnboundVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MEAN, var);
    }

    default T median(String var) {
        return median(UnboundVariable.named(var));
    }

    default T median(UnboundVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MEDIAN, var);
    }

    default T min(String var) {
        return min(UnboundVariable.named(var));
    }

    default T min(UnboundVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MIN, var);
    }

    default T std(String var) {
        return std(UnboundVariable.named(var));
    }

    default T std(UnboundVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.STD, var);
    }

    default T sum(String var) {
        return sum(UnboundVariable.named(var));
    }

    default T sum(UnboundVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.SUM, var);
    }

    T aggregate(TypeQLToken.Aggregate.Method method, UnboundVariable var);
}
