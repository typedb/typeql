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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.variable.TypeVariable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DEFINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.UNDEFINE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_DEFINABLES;

abstract class TypeQLDefinable extends TypeQLQuery {

    private final TypeQLToken.Command command;
    private final List<Definable> definables;
    private final List<TypeVariable> variables = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final int hash;

    TypeQLDefinable(TypeQLToken.Command command, List<Definable> definables) {
        assert command == DEFINE || command == UNDEFINE;
        if (definables == null || definables.isEmpty()) throw TypeQLException.of(MISSING_DEFINABLES.message());
        this.definables = new ArrayList<>(definables);
        for (Definable definable : definables) {
            if (definable.isRule()) {
                Rule rule = definable.asRule();
                if (command == UNDEFINE && (rule.when() != null || rule.then() != null)) {
                    throw TypeQLException.of(ErrorMessage.INVALID_UNDEFINE_QUERY_RULE.message(rule.label()));
                }
                rules.add(rule);
            }
            if (definable.isTypeVariable()) variables.add(definable.asTypeVariable());
        }
        LinkedList<TypeVariable> typeVarsToVerify = new LinkedList<>(variables);
        while (!typeVarsToVerify.isEmpty()) {
            TypeVariable v = typeVarsToVerify.removeFirst();
            if (!v.isLabelled()) throw TypeQLException.of(ErrorMessage.INVALID_DEFINE_QUERY_VARIABLE.message());
            else v.constraints().forEach(c -> typeVarsToVerify.addAll(c.variables()));
        }

        this.command = command;
        this.hash = Objects.hash(this.command, this.variables, this.rules);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    public final List<TypeVariable> variables() {
        return variables;
    }

    public final List<Rule> rules() {
        return rules;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder();
        appendClause(query, command, definables.stream().map(d -> d.toString(pretty)), pretty);
        return query.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeQLDefinable that = (TypeQLDefinable) o;
        return this.command.equals(that.command) && this.definables.equals(that.definables);
    }

    @Override
    public final int hashCode() {
        return hash;
    }
}
