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

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_SORTING_ORDER;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public interface Sortable<S, O, L> {

    default S sort(String var, String... vars) {
        return sort(concat(of(var), of(vars)).map(UnboundVariable::named).collect(Collectors.toList()));
    }

    default S sort(UnboundVariable var, UnboundVariable... vars) {
        return sort(concat(of(var), of(vars)).collect(Collectors.toList()));
    }

    default S sort(List<String> vars, String order) {
        TypeQLArg.Order o = TypeQLArg.Order.of(order);
        if (o == null) throw TypeQLException.of(
                INVALID_SORTING_ORDER.message(TypeQLArg.Order.ASC, TypeQLArg.Order.DESC)
        );
        return sort(vars.stream().map(UnboundVariable::named).collect(Collectors.toList()), o);
    }

    default S sort(List<UnboundVariable> vars) {
        return sort(new Sorting(vars));
    }

    default S sort(List<UnboundVariable> vars, TypeQLArg.Order order) {
        return sort(new Sorting(vars, order));
    }

    S sort(Sorting sorting);

    O offset(long offset);

    L limit(long limit);

    class Sorting {

        private final List<UnboundVariable> vars;
        private final TypeQLArg.Order order;
        private final int hash;

        public Sorting(List<UnboundVariable> vars) {
            this(vars, null);
        }

        public Sorting(List<UnboundVariable> vars, TypeQLArg.Order order) {
            this.vars = vars;
            this.order = order;
            this.hash = Objects.hash(vars(), order());
        }

        public List<UnboundVariable> vars() {
            return vars;
        }

        public TypeQLArg.Order order() {
            return order == null ? TypeQLArg.Order.ASC : order;
        }

        @Override
        public String toString() {
            StringBuilder sort = new StringBuilder();
            sort.append(vars.stream().map(Objects::toString).collect(COMMA_SPACE.joiner()));
            if (order != null) sort.append(SPACE).append(order);
            return sort.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sorting that = (Sorting) o;
            return (this.vars().equals(that.vars()) &&
                    this.order().equals(that.order()));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
