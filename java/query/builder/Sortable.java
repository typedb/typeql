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

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_NOT_SORTED;

public interface Sortable<S, O, L> {

    default S sort(String var, String... vars) {
        List<Pair<UnboundVariable, TypeQLArg.Order>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(UnboundVariable.named(var), null));
        for (String v : vars) pairs.add(new Pair<>(UnboundVariable.named(v), null));
        return sort(pairs);
    }

    default S sort(Pair<String, String> varOrder, Pair<String, String>... varOrders) {
        List<Pair<UnboundVariable, TypeQLArg.Order>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(UnboundVariable.named(varOrder.first()), TypeQLArg.Order.of(varOrder.second())));
        for (Pair<String, String> pair : varOrders) {
            pairs.add(new Pair<>(UnboundVariable.named(pair.first()), TypeQLArg.Order.of(pair.second())));
        }
        return sort(pairs);
    }

    default S sort(Pair<UnboundVariable, TypeQLArg.Order>... varOrders) {
        return sort(Arrays.asList(varOrders));
    }

    default S sort(List<Pair<UnboundVariable, TypeQLArg.Order>> varOrders) {
        return sort(Sorting.create(varOrders));
    }

    S sort(Sorting sorting);

    O offset(long offset);

    L limit(long limit);

    class Sorting {

        private final int hash;
        private final List<UnboundVariable> variables;
        private final Map<UnboundVariable, TypeQLArg.Order> orders;

        private Sorting(List<UnboundVariable> variables, Map<UnboundVariable, TypeQLArg.Order> orders) {
            this.variables = variables;
            this.orders = orders;
            this.hash = Objects.hash(variables, orders);
        }

        public static Sorting create(List<Pair<UnboundVariable, TypeQLArg.Order>> sorting) {
            List<UnboundVariable> vars = new ArrayList<>();
            Map<UnboundVariable, TypeQLArg.Order> orders = new HashMap<>();
            sorting.forEach(pair -> {
                vars.add(pair.first());
                orders.put(pair.first(), pair.second());
            });
            return new Sorting(vars, orders);
        }

        public List<UnboundVariable> variables() {
            return variables;
        }

        public TypeQLArg.Order getOrder(UnboundVariable var) {
            if (variables.contains(var)) throw TypeQLException.of(VARIABLE_NOT_SORTED.message(var));
            return orders.getOrDefault(var, TypeQLArg.Order.ASC);
        }

        @Override
        public String toString() {
            return variables.stream().map(v -> {
                if (orders.get(v) == null) return v.toString();
                else return v.toString() + SPACE + orders.get(v);
            }).collect(COMMA_SPACE.joiner());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sorting that = (Sorting) o;
            return this.variables.equals(that.variables) && this.orders.equals(that.orders);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
