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

package com.vaticle.typeql.lang.query.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.query.TypeQLQuery;

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
