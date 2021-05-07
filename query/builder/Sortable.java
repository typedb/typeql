/*
 * Copyright (C) 2021 Vaticle
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
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.Objects;

import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_SORTING_ORDER;

public interface Sortable<S, O, L> {

    default S sort(String var) {
        return sort(UnboundVariable.named(var));
    }

    default S sort(String var, String order) {
        TypeQLArg.Order o = TypeQLArg.Order.of(order);
        if (o == null) throw TypeQLException.of(
                INVALID_SORTING_ORDER.message(TypeQLArg.Order.ASC, TypeQLArg.Order.DESC)
        );
        return sort(UnboundVariable.named(var), o);
    }

    default S sort(String var, TypeQLArg.Order order) {
        return sort(UnboundVariable.named(var), order);
    }

    default S sort(UnboundVariable var) {
        return sort(new Sorting(var));
    }

    default S sort(UnboundVariable var, TypeQLArg.Order order) {
        return sort(new Sorting(var, order));
    }

    S sort(Sorting sorting);

    O offset(long offset);

    L limit(long limit);

    class Sorting {

        private final UnboundVariable var;
        private final TypeQLArg.Order order;
        private final int hash;

        public Sorting(UnboundVariable var) {
            this(var, null);
        }

        public Sorting(UnboundVariable var, TypeQLArg.Order order) {
            this.var = var;
            this.order = order;
            this.hash = Objects.hash(var(), order());
        }

        public UnboundVariable var() {
            return var;
        }

        public TypeQLArg.Order order() {
            return order == null ? TypeQLArg.Order.ASC : order;
        }

        @Override
        public String toString() {
            StringBuilder sort = new StringBuilder();

            sort.append(var);
            if (order != null) {
                sort.append(TypeQLToken.Char.SPACE).append(order);
            }

            return sort.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Sorting that = (Sorting) o;

            return (this.var().equals(that.var()) &&
                    this.order().equals(that.order()));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
