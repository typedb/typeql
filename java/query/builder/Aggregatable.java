/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.query.builder;

import com.typeql.lang.common.TypeQLToken;
import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.query.TypeQLQuery;

public interface Aggregatable<T extends TypeQLQuery> {

    default T count() {
        return aggregate(TypeQLToken.Aggregate.Method.COUNT, null);
    }

    default T max(TypeQLVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MAX, var);
    }

    default T mean(TypeQLVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MEAN, var);
    }

    default T median(TypeQLVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MEDIAN, var);
    }

    default T min(TypeQLVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.MIN, var);
    }

    default T std(TypeQLVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.STD, var);
    }

    default T sum(TypeQLVariable var) {
        return aggregate(TypeQLToken.Aggregate.Method.SUM, var);
    }

    T aggregate(TypeQLToken.Aggregate.Method method, TypeQLVariable var);
}
