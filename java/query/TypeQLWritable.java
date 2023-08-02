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
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public abstract class TypeQLWritable implements TypeQLQuery {

    protected final MatchClause match;

    TypeQLWritable(@Nullable MatchClause match) {
        this.match = match;
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    abstract static class InsertOrDelete extends TypeQLWritable {

        private List<UnboundVariable> namedVariablesUnbound;
        private final TypeQLToken.Command command;
        protected final List<ThingVariable<?>> variables;
        private final Modifiers modifiers;
        private final int hash;

        InsertOrDelete(TypeQLToken.Command command, @Nullable MatchClause match, List<ThingVariable<?>> variables, Modifiers modifiers) {
            super(match);
            assert command == INSERT || command == DELETE;
            if (variables == null || variables.isEmpty()) throw TypeQLException.of(MISSING_PATTERNS.message());
            this.command = command;
            this.variables = variables;
            this.modifiers = modifiers;
            this.hash = Objects.hash(this.command, this.match, this.variables);
        }

        public List<UnboundVariable> namedVariablesUnbound() {
            if (namedVariablesUnbound == null) {
                namedVariablesUnbound = variables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                        .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
            }
            return namedVariablesUnbound;
        }

        public Modifiers modifiers() {
            return modifiers;
        }

        @Override
        public String toString(boolean pretty) {
            StringBuilder query = new StringBuilder();
            if (match != null) query.append(match.toString(pretty)).append(NEW_LINE);
            appendClause(query, command, variables.stream().map(v -> v.toString(pretty)), pretty);
            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InsertOrDelete that = (InsertOrDelete) o;
            return (this.command.equals(that.command) &&
                    Objects.equals(this.match, that.match) &&
                    this.variables.equals(that.variables));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
