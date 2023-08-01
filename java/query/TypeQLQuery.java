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
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.builder.Sortable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.GET;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.LIMIT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.OFFSET;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.SORT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public abstract class TypeQLQuery {

    public abstract TypeQLArg.QueryType type();

    public TypeQLDefine asDefine() {
        if (this instanceof TypeQLDefine) {
            return (TypeQLDefine) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLDefine.class)));
        }
    }

    public TypeQLUndefine asUndefine() {
        if (this instanceof TypeQLUndefine) {
            return (TypeQLUndefine) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLUndefine.class)));
        }
    }

    public TypeQLInsert asInsert() {
        if (this instanceof TypeQLInsert) {
            return (TypeQLInsert) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLInsert.class)));
        }
    }

    public TypeQLDelete asDelete() {
        if (this instanceof TypeQLDelete) {
            return (TypeQLDelete) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLDelete.class)));
        }
    }

    public TypeQLUpdate asUpdate() {
        if (this instanceof TypeQLUpdate) {
            return (TypeQLUpdate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLUpdate.class)));
        }
    }

    public TypeQLGet asMatch() {
        if (this instanceof TypeQLGet) {
            return (TypeQLGet) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.class)));
        }
    }

    public TypeQLGet.Aggregate asMatchAggregate() {
        if (this instanceof TypeQLGet.Aggregate) {
            return (TypeQLGet.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Aggregate.class)));
        }
    }

    public TypeQLGet.Group asMatchGroup() {
        if (this instanceof TypeQLGet.Group) {
            return (TypeQLGet.Group) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Group.class)));
        }
    }

    public TypeQLGet.Group.Aggregate asMatchGroupAggregate() {
        if (this instanceof TypeQLGet.Group.Aggregate) {
            return (TypeQLGet.Group.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Group.Aggregate.class)));
        }
    }

    protected void appendSubQuery(StringBuilder query, TypeQLToken.Command command, Stream<String> elements, boolean pretty) {
        query.append(command).append(NEW_LINE);
        if (pretty) query.append(elements.collect(SEMICOLON_NEW_LINE.joiner()));
        else query.append(elements.collect(SEMICOLON_SPACE.joiner()));
        query.append(SEMICOLON);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public abstract String toString(boolean pretty);

    static class MatchClause {

        private final Conjunction<? extends Pattern> conjunction;

        private List<BoundVariable> variables;
        private List<UnboundVariable> variablesNamedUnbound;

        MatchClause(Conjunction<? extends Pattern> conjunction) {
            this.conjunction = conjunction;

            // TODO: these can be minimised
            hasBoundingConjunction();
            nestedPatternsAreBounded();
            queryHasNamedVariable();
            eachPatternVariableHasNamedVariable(conjunction.patterns());
        }

        private void hasBoundingConjunction() {
            if (!conjunction.namedVariablesUnbound().findAny().isPresent()) {
                throw TypeQLException.of(MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE);
            }
        }

        private void nestedPatternsAreBounded() {
            conjunction.patterns().stream().filter(pattern -> !pattern.isVariable()).forEach(pattern -> {
                pattern.validateIsBoundedBy(conjunction.namedVariablesUnbound().collect(toSet()));
            });
        }

        private void queryHasNamedVariable() {
            if (namedVariablesUnbound().isEmpty()) throw TypeQLException.of(MATCH_HAS_NO_NAMED_VARIABLE);
        }

        private void eachPatternVariableHasNamedVariable(List<? extends Pattern> patterns) {
            patterns.forEach(pattern -> {
                if (pattern.isVariable() && !pattern.asVariable().isNamed()
                        && pattern.asVariable().variables().noneMatch(constraintVar -> constraintVar.isNamed())) {
                    throw TypeQLException.of(MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE.message(pattern));
                } else if (!pattern.isVariable()) {
                    eachPatternVariableHasNamedVariable(pattern.patterns());
                }
            });
        }

        public Stream<Pattern> patternsRecursive() {
            return patternsRecursive(conjunction);
        }

        private Stream<Pattern> patternsRecursive(Pattern pattern) {
            return Stream.concat(Stream.of(pattern), pattern.patterns().stream().filter(p -> !p.equals(pattern))
                    .flatMap(this::patternsRecursive));
        }

        public Conjunction<? extends Pattern> conjunction() {
            return conjunction;
        }

        public List<BoundVariable> variables() {
            if (variables == null) variables = conjunction.variables().collect(toList());
            return variables;
        }

        public List<UnboundVariable> namedVariablesUnbound() {
            if (variablesNamedUnbound == null) {
                variablesNamedUnbound = conjunction.namedVariablesUnbound().collect(toList());
            }
            return variablesNamedUnbound;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (getClass() != o.getClass()) return false;
            MatchClause that = (MatchClause) o;
            return this.conjunction.equals(that.conjunction);
        }

        @Override
        public int hashCode() {
            return conjunction.hashCode();
        }
    }

    public static class Modifiers {

        final TypeQLGet typeQLGet;
        final List<UnboundVariable> filter;
        final Sortable.Sorting sorting;
        final Long offset;
        final Long limit;

        private final int hash;

        public Modifiers(TypeQLGet typeQLGet, List<UnboundVariable> filter, @Nullable Sortable.Sorting sorting, @Nullable Long offset,
                         @Nullable Long limit) {
            this.typeQLGet = typeQLGet;
            this.filter = list(filter);
            this.sorting = sorting;
            this.offset = offset;
            this.limit = limit;
            this.hash = Objects.hash(this.filter, this.sorting, this.offset, this.limit);
        }

        public List<UnboundVariable> filter() {
            if (filter.isEmpty()) return typeQLGet.namedVariablesUnbound();
            else return filter;
        }

        public Optional<Long> offset() {
            return Optional.ofNullable(offset);
        }

        public Optional<Long> limit() {
            return Optional.ofNullable(limit);
        }

        public Optional<Sortable.Sorting> sort() {
            return Optional.ofNullable(sorting);
        }

        public boolean isEmpty() {
            return filter.isEmpty() && sorting == null && offset == null && limit == null;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            if (!filter.isEmpty()) {
                syntax.append(GET);
                String vars = filter.stream().map(UnboundVariable::toString).collect(COMMA_SPACE.joiner());
                syntax.append(SPACE).append(vars);
                syntax.append(SEMICOLON).append(SPACE);
            }
            if (sorting != null) syntax.append(SORT).append(SPACE).append(sorting).append(SEMICOLON).append(SPACE);
            if (offset != null) syntax.append(OFFSET).append(SPACE).append(offset).append(SEMICOLON).append(SPACE);
            if (limit != null) syntax.append(LIMIT).append(SPACE).append(limit).append(SEMICOLON).append(SPACE);
            return syntax.toString().trim();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Modifiers modifiers = (Modifiers) o;
            return Objects.equals(filter, modifiers.filter) && Objects.equals(sorting, modifiers.sorting)
                    && Objects.equals(offset, modifiers.offset) && Objects.equals(limit, modifiers.limit);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
