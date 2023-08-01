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
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.builder.Aggregatable;
import com.vaticle.typeql.lang.query.builder.Sortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.GROUP;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.MATCH;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.FILTER_VARIABLE_ANONYMOUS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_FILTER_VARIABLE_REPEATING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_COUNT_VARIABLE_ARGUMENT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_PATTERN_VARIABLE_HAS_NO_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_MATCH;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class TypeQLGet extends TypeQLQuery implements Aggregatable<TypeQLGet.Aggregate> {

    final MatchClause match;
    private final Modifiers modifiers;

    private final int hash;


    TypeQLGet(MatchClause match) {
        this(match, new ArrayList<>());
    }

    TypeQLGet(MatchClause match, List<UnboundVariable> filter) {
        this(match, filter, null, null, null);
    }

    public TypeQLGet(MatchClause match, List<UnboundVariable> filter, Sortable.Sorting sorting, Long offset, Long limit) {
        if (filter == null) throw TypeQLException.of(ErrorMessage.MISSING_MATCH_FILTER.message());
        this.match = match;
        this.modifiers = new Modifiers(this, filter, sorting, offset, limit);

        filtersAreInScope();
        sortVarsAreInScope();
        validateNamesUnique(match.patternsRecursive());
        this.hash = Objects.hash(this.match, this.modifiers);
    }

    private void filtersAreInScope() {
        Set<UnboundVariable> duplicates = new HashSet<>();
        for (UnboundVariable var : modifiers.filter) {
            if (!var.isNamed()) throw TypeQLException.of(FILTER_VARIABLE_ANONYMOUS);
            if (!match.namedVariablesUnbound().contains(var))
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var));
            if (duplicates.contains(var)) throw TypeQLException.of(ILLEGAL_FILTER_VARIABLE_REPEATING.message(var));
            else duplicates.add(var);
        }
    }

    private void sortVarsAreInScope() {
        List<UnboundVariable> sortableVars = modifiers.filter.isEmpty() ? match.namedVariablesUnbound() : modifiers.filter;
        if (modifiers.sorting != null && modifiers.sorting.variables().stream().anyMatch(v -> !sortableVars.contains(v))) {
            throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(modifiers.sorting.variables()));
        }
    }

    @Override
    public Aggregate aggregate(TypeQLToken.Aggregate.Method method, UnboundVariable var) {
        return new Aggregate(this, method, var);
    }

    public Group group(UnboundVariable var) {
        return new Group(this, var);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.READ;
    }

    public Modifiers modifiers() {
        return modifiers;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder();
        appendSubQuery(query, MATCH, conjunction.patterns().stream().map(p -> p.toString(pretty)), pretty);
        if (!modifiers.isEmpty()) {
            if (pretty) query.append(NEW_LINE);
            query.append(modifiers);
        }
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }
        TypeQLGet that = (TypeQLGet) o;
        return Objects.equals(this.match, that.match) && Objects.equals(this.modifiers, that.modifiers);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unfiltered extends TypeQLGet implements Sortable<Sorted, Offset, Limited> {

        public Unfiltered(List<? extends Pattern> patterns) {
            super(validConjunction(patterns));
        }

        static Conjunction<? extends Pattern> validConjunction(List<? extends Pattern> patterns) {
            if (patterns.size() == 0) throw TypeQLException.of(MISSING_PATTERNS.message());
            return new Conjunction<>(patterns);
        }

        public TypeQLGet.Filtered get(UnboundVariable var, UnboundVariable... vars) {
            List<UnboundVariable> varList = new ArrayList<>();
            varList.add(var);
            varList.addAll(list(vars));
            return get(varList);
        }

        public TypeQLGet.Filtered get(List<UnboundVariable> vars) {
            return new TypeQLGet.Filtered(this, vars);
        }

        @Override
        public TypeQLGet.Sorted sort(Sorting sorting) {
            return new TypeQLGet.Sorted(this, sorting);
        }

        @Override
        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        @Override
        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
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

    public static class Filtered extends TypeQLGet implements Sortable<Sorted, Offset, Limited> {

        public Filtered(Unfiltered unfiltered, List<UnboundVariable> filter) {
            super(unfiltered.conjunction(), filter, null, null, null);
            if (filter.isEmpty()) throw TypeQLException.of(ErrorMessage.EMPTY_MATCH_FILTER);
        }

        @Override
        public TypeQLGet.Sorted sort(Sorting sorting) {
            return new TypeQLGet.Sorted(this, sorting);
        }

        @Override
        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        @Override
        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLGet {

        public Sorted(TypeQLGet match, Sortable.Sorting sorting) {
            super(match.conjunction, match.modifiers.filter, sorting, match.modifiers.offset, match.modifiers.limit);
        }

        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLGet {

        public Offset(TypeQLGet match, long offset) {
            super(match.conjunction, match.modifiers.filter, match.modifiers.sorting, offset, match.modifiers.limit);
        }

        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLGet {

        public Limited(TypeQLGet match, long limit) {
            super(match.conjunction, match.modifiers.filter, match.modifiers.sorting, match.modifiers.offset, limit);
        }
    }

    public static class Aggregate extends TypeQLQuery {

        private final TypeQLGet query;
        private final TypeQLToken.Aggregate.Method method;
        private final UnboundVariable var;
        private final int hash;

        public Aggregate(TypeQLGet query, TypeQLToken.Aggregate.Method method, UnboundVariable var) {
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

        public TypeQLGet match() {
            return query;
        }

        public TypeQLToken.Aggregate.Method method() {
            return method;
        }

        public UnboundVariable var() {
            return var;
        }

        @Override
        public final String toString(boolean pretty) {
            StringBuilder query = new StringBuilder();
            query.append(match().toString(pretty));
            if (pretty) query.append(NEW_LINE);
            query.append(method);
            if (var != null) query.append(SPACE).append(var.toString(pretty));
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

        private final TypeQLGet query;
        private final UnboundVariable var;
        private final int hash;

        public Group(TypeQLGet query, UnboundVariable var) {
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

        public TypeQLGet match() {
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
        public String toString(boolean pretty) {
            if (pretty) return match().toString(pretty) + NEW_LINE + GROUP + SPACE + var + SEMICOLON;
            else return match().toString(pretty) + GROUP + SPACE + var + SEMICOLON;
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

            private final TypeQLGet.Group group;
            private final TypeQLToken.Aggregate.Method method;
            private final UnboundVariable var;
            private final int hash;

            public Aggregate(TypeQLGet.Group group, TypeQLToken.Aggregate.Method method, UnboundVariable var) {
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

            public TypeQLGet.Group group() {
                return group;
            }

            public TypeQLToken.Aggregate.Method method() {
                return method;
            }

            public UnboundVariable var() {
                return var;
            }

            @Override
            public final String toString(boolean pretty) {
                StringBuilder query = new StringBuilder();
                query.append(group().match().toString(pretty));
                if (pretty) query.append(NEW_LINE);
                query.append(GROUP).append(SPACE).append(group().var().toString(pretty))
                        .append(SEMICOLON).append(SPACE).append(method);
                if (var != null) query.append(SPACE).append(var.toString(pretty));
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
