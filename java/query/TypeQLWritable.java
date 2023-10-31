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
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.statement.Statement;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;
import static java.util.stream.Collectors.toList;

public abstract class TypeQLWritable implements TypeQLQuery {

    protected final MatchClause match;

    TypeQLWritable(@Nullable MatchClause match) {
        this.match = match;
    }

    public Optional<MatchClause> match() {
        return Optional.ofNullable(match);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    abstract static class InsertOrDelete extends TypeQLWritable {

        private List<TypeQLVariable> namedVariables;
        private final TypeQLToken.Clause clause;
        final List<ThingStatement<?>> statements;
        final Modifiers modifiers;
        private final int hash;

        InsertOrDelete(TypeQLToken.Clause clause, @Nullable MatchClause match, List<ThingStatement<?>> statements, Modifiers modifiers) {
            super(match);
            assert clause == INSERT || clause == DELETE;
            assert modifiers != null;
            if (statements == null || statements.isEmpty()) throw TypeQLException.of(MISSING_PATTERNS.message());
            this.clause = clause;
            this.statements = statements;
            this.modifiers = modifiers;
            this.hash = Objects.hash(this.clause, this.match, this.statements, this.modifiers);
        }

        public List<TypeQLVariable> namedVariables() {
            if (namedVariables == null) {
                namedVariables = statements.stream().flatMap(Statement::variables)
                        .filter(TypeQLVariable::isNamed).distinct().collect(toList());
            }
            return namedVariables;
        }

        public Modifiers modifiers() {
            return modifiers;
        }

        public List<ThingStatement<?>> statements() {
            return statements;
        }

        @Override
        public String toString(boolean pretty) {
            StringBuilder query = new StringBuilder();
            if (match != null) query.append(match.toString(pretty)).append(NEW_LINE);
            appendClause(query, clause, statements.stream().map(v -> v.toString(pretty)), pretty);
            appendModifiers(query, modifiers, pretty);
            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
                return false;
            }
            InsertOrDelete that = (InsertOrDelete) o;
            return (this.clause.equals(that.clause) &&
                    Objects.equals(this.match, that.match) &&
                    this.statements.equals(that.statements)) &&
                    this.modifiers.equals(that.modifiers);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
