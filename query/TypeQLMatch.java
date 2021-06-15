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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Negation;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.builder.Aggregatable;
import com.vaticle.typeql.lang.query.builder.Sortable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.GROUP;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.GET;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.LIMIT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.OFFSET;
import static com.vaticle.typeql.lang.common.TypeQLToken.Filter.SORT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_FILTER_VARIABLE_REPEATING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_COUNT_VARIABLE_ARGUMENT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_NOT_NAMED;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_MATCH;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class TypeQLMatch extends TypeQLQuery implements Aggregatable<TypeQLMatch.Aggregate> {

    private final Conjunction<? extends Pattern> conjunction;
    private final Modifiers modifiers;

    private final int hash;

    private List<BoundVariable> variables;
    private List<UnboundVariable> variablesNamedUnbound;

    TypeQLMatch(Conjunction<? extends Pattern> conjunction) {
        this(conjunction, new ArrayList<>());
    }

    TypeQLMatch(Conjunction<? extends Pattern> conjunction, List<UnboundVariable> filter) {
        this(conjunction, filter, null, null, null);
    }

    public TypeQLMatch(Conjunction<? extends Pattern> conjunction, List<UnboundVariable> filter, Sortable.Sorting sorting, Long offset, Long limit) {
        if (filter == null) throw TypeQLException.of(ErrorMessage.MISSING_MATCH_FILTER.message());
        this.conjunction = conjunction;
        this.modifiers = new Modifiers(filter, sorting, offset, limit);

        hasBoundingConjunction();
        nestedPatternsAreBounded();
        queryHasNamedVariable();
        eachPatternVariableHasNamedVariable(conjunction.patterns());
        filtersAreInScope();
        sortVarsAreInScope();

        this.hash = Objects.hash(this.conjunction, this.modifiers);
    }


    public class Modifiers {

        private final List<UnboundVariable> filter;
        private final Sortable.Sorting sorting;
        private final Long offset;
        private final Long limit;

        private final int hash;

        public Modifiers(List<UnboundVariable> filter, @Nullable Sortable.Sorting sorting, @Nullable Long offset,
                         @Nullable Long limit) {
            this.filter = list(filter);
            this.sorting = sorting;
            this.offset = offset;
            this.limit = limit;
            this.hash = Objects.hash(this.filter, this.sorting, this.offset, this.limit);
        }

        public List<UnboundVariable> filter() {
            if (filter.isEmpty()) return namedVariablesUnbound();
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
                String varsStr = filter.stream().map(UnboundVariable::toString).collect(joining(COMMA_SPACE.toString()));
                syntax.append(SPACE).append(varsStr);
                syntax.append(SEMICOLON);
            }
            if (sorting != null) syntax.append(SORT).append(SPACE).append(sorting).append(SEMICOLON).append(SPACE);
            if (offset != null) syntax.append(OFFSET).append(SPACE).append(offset).append(SEMICOLON).append(SPACE);
            if (limit != null) syntax.append(LIMIT).append(SPACE).append(limit).append(SEMICOLON).append(SPACE);
            return syntax.toString();
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
            if (pattern.isVariable() && !pattern.asVariable().reference().isName()
                    && pattern.asVariable().variables().noneMatch(constraintVar -> constraintVar.reference().isName())) {
                throw TypeQLException.of(MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE.message(pattern));
            } else if (!pattern.isVariable()) {
                eachPatternVariableHasNamedVariable(pattern.patterns());
            }
        });
    }

    private void filtersAreInScope() {
        Set<UnboundVariable> duplicates = new HashSet<>();
        for (UnboundVariable var : modifiers.filter) {
            if (!namedVariablesUnbound().contains(var))
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var));
            if (!var.isNamed()) throw TypeQLException.of(VARIABLE_NOT_NAMED.message(var));
            if (duplicates.contains(var)) throw TypeQLException.of(ILLEGAL_FILTER_VARIABLE_REPEATING);
            else duplicates.add(var);
        }
    }

    private void sortVarsAreInScope() {
        List<UnboundVariable> sortableVars = modifiers.filter.isEmpty() ? namedVariablesUnbound() : modifiers.filter;
        if (modifiers.sorting != null && !sortableVars.contains(modifiers.sorting.var())) {
            throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(modifiers.sorting.var()));
        }
    }

    @Override
    public Aggregate aggregate(TypeQLToken.Aggregate.Method method, UnboundVariable var) {
        return new Aggregate(this, method, var);
    }

    public Group group(String var) {
        return group(UnboundVariable.named(var));
    }

    public Group group(UnboundVariable var) {
        return new Group(this, var);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.READ;
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

    public Modifiers modifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder();
        query.append(TypeQLToken.Command.MATCH);

        if (conjunction.patterns().size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);
        query.append(conjunction.patterns().stream().map(Object::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);

        if (!modifiers.isEmpty()) {
            if (conjunction.patterns().size() > 1) query.append(NEW_LINE);
            else query.append(SPACE);
            query.append(modifiers.toString());
        }

        return query.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }
        TypeQLMatch that = (TypeQLMatch) o;
        return Objects.equals(this.conjunction, that.conjunction) && Objects.equals(this.modifiers, that.modifiers);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unfiltered extends TypeQLMatch implements Sortable<Sorted, Offset, Limited> {

        public Unfiltered(List<? extends Pattern> patterns) {
            super(validConjunction(patterns));
        }

        static Conjunction<? extends Pattern> validConjunction(List<? extends Pattern> patterns) {
            if (patterns.size() == 0) throw TypeQLException.of(MISSING_PATTERNS.message());
            return new Conjunction<>(patterns);
        }

        public TypeQLMatch.Filtered get(String var, String... vars) {
            return get(concat(of(var), of(vars)).map(UnboundVariable::named).collect(toList()));
        }

        public TypeQLMatch.Filtered get(UnboundVariable var, UnboundVariable... vars) {
            List<UnboundVariable> varList = new ArrayList<>();
            varList.add(var);
            varList.addAll(Arrays.asList(vars));
            return get(varList);
        }

        public TypeQLMatch.Filtered get(List<UnboundVariable> vars) {
            return new TypeQLMatch.Filtered(this, vars);
        }

        @Override
        public TypeQLMatch.Sorted sort(Sorting sorting) {
            return new TypeQLMatch.Sorted(this, sorting);
        }

        @Override
        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        @Override
        public TypeQLMatch.Limited limit(long limit) {
            return new TypeQLMatch.Limited(this, limit);
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
    }

    public static class Filtered extends TypeQLMatch implements Sortable<Sorted, Offset, Limited> {

        public Filtered(Unfiltered unfiltered, List<UnboundVariable> filter) {
            super(unfiltered.conjunction(), filter, null, null, null);
            if (filter.isEmpty()) throw TypeQLException.of(ErrorMessage.EMPTY_MATCH_FILTER);
        }

        @Override
        public TypeQLMatch.Sorted sort(Sorting sorting) {
            return new TypeQLMatch.Sorted(this, sorting);
        }

        @Override
        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        @Override
        public TypeQLMatch.Limited limit(long limit) {
            return new TypeQLMatch.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLMatch {

        public Sorted(TypeQLMatch match, Sortable.Sorting sorting) {
            super(match.conjunction, match.modifiers.filter, sorting, match.modifiers.offset, match.modifiers.limit);
        }

        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        public TypeQLMatch.Limited limit(long limit) {
            return new TypeQLMatch.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLMatch {

        public Offset(TypeQLMatch match, long offset) {
            super(match.conjunction, match.modifiers.filter, match.modifiers.sorting, offset, match.modifiers.limit);
        }

        public TypeQLMatch.Limited limit(long limit) {
            return new TypeQLMatch.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLMatch {

        public Limited(TypeQLMatch match, long limit) {
            super(match.conjunction, match.modifiers.filter, match.modifiers.sorting, match.modifiers.offset, limit);
        }
    }

    public static class Aggregate extends TypeQLQuery {

        private final TypeQLMatch query;
        private final TypeQLToken.Aggregate.Method method;
        private final UnboundVariable var;
        private final int hash;

        public Aggregate(TypeQLMatch query, TypeQLToken.Aggregate.Method method, UnboundVariable var) {
            if (query == null) throw new NullPointerException("MatchQuery is null");
            if (method == null) throw new NullPointerException("Method is null");

            if (var == null && !method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                throw new NullPointerException("Variable is null");
            } else if (var != null && method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                throw TypeQLException.of(INVALID_COUNT_VARIABLE_ARGUMENT.message());
            } else if (var != null && !query.modifiers.filter().contains(var)) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var.toString()));
            }

            this.query = query;
            this.method = method;
            this.var = var;
            this.hash = Objects.hash(query, method, var);
        }

        @Override
        public TypeQLArg.QueryType type() {
            return TypeQLArg.QueryType.READ;
        }

        public TypeQLMatch match() {
            return query;
        }

        public TypeQLToken.Aggregate.Method method() {
            return method;
        }

        public UnboundVariable var() {
            return var;
        }

        @Override
        public final String toString() {
            StringBuilder query = new StringBuilder();

            if (match().modifiers().filter().isEmpty() && match().conjunction().patterns().size() > 1) {
                query.append(match()).append(NEW_LINE);
            } else query.append(match()).append(SPACE);

            query.append(method);
            if (var != null) query.append(SPACE).append(var);
            query.append(SEMICOLON);

            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Aggregate that = (Aggregate) o;
            return this.query.equals(that.query) && this.method.equals(that.method) && Objects.equals(this.var, that.var);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Group extends TypeQLQuery implements Aggregatable<Group.Aggregate> {

        private final TypeQLMatch query;
        private final UnboundVariable var;
        private final int hash;

        public Group(TypeQLMatch query, UnboundVariable var) {
            if (query == null) throw new NullPointerException("GetQuery is null");
            if (var == null) throw new NullPointerException("Variable is null");
            else if (!query.modifiers.filter().contains(var)) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var.toString()));
            }

            this.query = query;
            this.var = var;
            this.hash = Objects.hash(query, var);
        }

        @Override
        public TypeQLArg.QueryType type() {
            return TypeQLArg.QueryType.READ;
        }

        public TypeQLMatch match() {
            return query;
        }

        public UnboundVariable var() {
            return var;
        }

        @Override
        public Aggregate aggregate(TypeQLToken.Aggregate.Method method, UnboundVariable var) {
            return new Aggregate(this, method, var);
        }

        @Override
        public String toString() {
            StringBuilder query = new StringBuilder();

            if (match().modifiers().filter.isEmpty() && match().conjunction().patterns().size() > 1) {
                query.append(match()).append(NEW_LINE);
            } else query.append(match()).append(SPACE);

            query.append(GROUP).append(SPACE).append(var).append(SEMICOLON);
            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Group that = (Group) o;
            return this.query.equals(that.query) && this.var.equals(that.var);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public static class Aggregate extends TypeQLQuery {

            private final TypeQLMatch.Group group;
            private final TypeQLToken.Aggregate.Method method;
            private final UnboundVariable var;
            private final int hash;

            public Aggregate(TypeQLMatch.Group group, TypeQLToken.Aggregate.Method method, UnboundVariable var) {
                if (group == null) throw new NullPointerException("TypeQLMatch.Group is null");
                if (method == null) throw new NullPointerException("Method is null");
                if (var == null && !method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                    throw new NullPointerException("Variable is null");
                } else if (var != null && method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                    throw new IllegalArgumentException(INVALID_COUNT_VARIABLE_ARGUMENT.message());
                } else if (var != null && !group.query.modifiers.filter().contains(var)) {
                    throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var.toString()));
                }

                this.group = group;
                this.method = method;
                this.var = var;
                this.hash = Objects.hash(group, method, var);
            }

            @Override
            public TypeQLArg.QueryType type() {
                return TypeQLArg.QueryType.READ;
            }

            public TypeQLMatch.Group group() {
                return group;
            }

            public TypeQLToken.Aggregate.Method method() {
                return method;
            }

            public UnboundVariable var() {
                return var;
            }

            @Override
            public final String toString() {
                StringBuilder query = new StringBuilder();

                if (group().match().modifiers().filter.isEmpty() && group().match().conjunction().patterns().size() > 1) {
                    query.append(group().match()).append(NEW_LINE);
                } else query.append(group().match()).append(SPACE);

                query.append(GROUP).append(SPACE).append(group().var()).append(SEMICOLON).append(SPACE).append(method);

                if (var != null) query.append(SPACE).append(var);
                query.append(SEMICOLON);

                return query.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Aggregate that = (Aggregate) o;
                return this.group.equals(that.group) && this.method.equals(that.method)
                        && Objects.equals(this.var, that.var);
            }

            @Override
            public int hashCode() {
                return hash;
            }
        }
    }
}
