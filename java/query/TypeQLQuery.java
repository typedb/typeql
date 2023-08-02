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
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.builder.Sortable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.MATCH;
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

public interface TypeQLQuery {

    TypeQLArg.QueryType type();

    default TypeQLDefine asDefine() {
        if (this instanceof TypeQLDefine) {
            return (TypeQLDefine) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLDefine.class)));
        }
    }

    default TypeQLUndefine asUndefine() {
        if (this instanceof TypeQLUndefine) {
            return (TypeQLUndefine) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLUndefine.class)));
        }
    }

    default TypeQLInsert asInsert() {
        if (this instanceof TypeQLInsert) {
            return (TypeQLInsert) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLInsert.class)));
        }
    }

    default TypeQLDelete asDelete() {
        if (this instanceof TypeQLDelete) {
            return (TypeQLDelete) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLDelete.class)));
        }
    }

    default TypeQLUpdate asUpdate() {
        if (this instanceof TypeQLUpdate) {
            return (TypeQLUpdate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLUpdate.class)));
        }
    }

    default TypeQLGet asMatch() {
        if (this instanceof TypeQLGet) {
            return (TypeQLGet) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.class)));
        }
    }

    default TypeQLGet.Aggregate asMatchAggregate() {
        if (this instanceof TypeQLGet.Aggregate) {
            return (TypeQLGet.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Aggregate.class)));
        }
    }

    default TypeQLGet.Group asMatchGroup() {
        if (this instanceof TypeQLGet.Group) {
            return (TypeQLGet.Group) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Group.class)));
        }
    }

    default TypeQLGet.Group.Aggregate asMatchGroupAggregate() {
        if (this instanceof TypeQLGet.Group.Aggregate) {
            return (TypeQLGet.Group.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Group.Aggregate.class)));
        }
    }

    static void appendClause(StringBuilder builder, TypeQLToken.Command command, Stream<String> elements, boolean pretty) {
        builder.append(command).append(NEW_LINE);
        if (pretty) builder.append(elements.collect(SEMICOLON_NEW_LINE.joiner()));
        else builder.append(elements.collect(SEMICOLON_SPACE.joiner()));
        builder.append(SEMICOLON);
    }

    @Override
    default String toString() {
        return toString(true);
    }

    String toString(boolean pretty);

    interface Unmodified extends TypeQLQuery {

        Sorted sort(Sortable.Sorting sorting);

        Offsetted offset(long offset);

        Limited limit(long limit);
    }

    interface Sorted extends TypeQLQuery {

        Modifiers modifiers();

        Offsetted offset(long offset);

        Limited limit(long limit);
    }

    interface Offsetted extends TypeQLQuery {

        Modifiers modifiers();

        Limited limit(long offset);
    }

    interface Limited extends TypeQLQuery {

        Modifiers modifiers();
    }

    class MatchClause {

        private final Conjunction<? extends Pattern> conjunction;

        private List<BoundVariable> variables;
        private Set<UnboundVariable> variablesNamedUnbound;

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

        public TypeQLGet.Unmodified get(UnboundVariable var, UnboundVariable... vars) {
            List<UnboundVariable> varList = new ArrayList<>();
            varList.add(var);
            varList.addAll(list(vars));
            return get(varList);
        }

        public TypeQLGet.Unmodified get(List<UnboundVariable> vars) {
            return new TypeQLGet.Unmodified(this, vars);
        }

        public TypeQLInsert insert(ThingVariable<?>... things) {
            return insert(list(things));
        }

        public TypeQLInsert insert(List<ThingVariable<?>> things) {
            return new TypeQLInsert(this, things);
        }

        public TypeQLDelete delete(ThingVariable<?>... things) {
            return delete(list(things));
        }

        public TypeQLDelete delete(List<ThingVariable<?>> things) {
            return new TypeQLDelete(this, things);
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

        public Set<UnboundVariable> namedVariablesUnbound() {
            if (variablesNamedUnbound == null) {
                variablesNamedUnbound = conjunction.namedVariablesUnbound().collect(toSet());
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

        @Override
        public String toString() {
            return toString(true);
        }

        public String toString(boolean pretty) {
            StringBuilder query = new StringBuilder();
            appendClause(query, MATCH, conjunction.patterns().stream().map(p -> p.toString(pretty)), pretty);
            return query.toString();
        }
    }

    class Modifiers {

        static Modifiers EMPTY = new Modifiers(null, null, null);

        final Sortable.Sorting sorting;
        final Long offset;
        final Long limit;

        private final int hash;

        public Modifiers(@Nullable Sortable.Sorting sorting, @Nullable Long offset, @Nullable Long limit) {
            this.sorting = sorting;
            this.offset = offset;
            this.limit = limit;
            this.hash = Objects.hash(this.sorting, this.offset, this.limit);
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
            sorting == null && offset == null && limit == null;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
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
            return Objects.equals(sorting, modifiers.sorting)
                    && Objects.equals(offset, modifiers.offset) && Objects.equals(limit, modifiers.limit);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
