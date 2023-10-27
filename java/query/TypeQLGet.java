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
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.query.builder.Aggregatable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.GET;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.GROUP;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.FILTER_VARIABLE_ANONYMOUS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_FILTER_VARIABLE_REPEATING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_COUNT_VARIABLE_ARGUMENT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class TypeQLGet implements TypeQLQuery, Aggregatable<TypeQLGet.Aggregate> {

    final MatchClause match;
    final Modifiers modifiers;
    final List<TypeQLVariable> filter;
    private final List<TypeQLVariable> effectiveFilter;
    private final int hash;

    public TypeQLGet(MatchClause match, List<TypeQLVariable> filter, Modifiers modifiers) {
        if (filter == null) throw TypeQLException.of(ErrorMessage.MISSING_GET_FILTER.message());
        this.match = match;
        if (filter.isEmpty()) {
            this.filter = emptyList();
            this.effectiveFilter = list(match.namedVariables());
        } else {
            this.filter = unmodifiableList(filter);
            this.effectiveFilter = this.filter;
            filtersAreInScope();
        }
        this.modifiers = modifiers;

        sortVarsAreInScope(); // TODO: this is redundant each time we update a modifier
        validateNamesUnique(match.patternsRecursive()); // TODO: this is redundant each time we update a modifier
        this.hash = Objects.hash(this.match, this.filter, this.modifiers);
    }

    private void sortVarsAreInScope() {
        Collection<TypeQLVariable> sortableVars = filter.isEmpty() ? match.namedVariables() : filter;
        if (modifiers.sorting != null && modifiers.sorting.variables().stream().anyMatch(v -> !sortableVars.contains(v))) {
            throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE.message(modifiers.sorting.variables()));
        }
    }

    private void filtersAreInScope() {
        Set<TypeQLVariable> duplicates = new HashSet<>();
        for (TypeQLVariable var : filter) {
            if (!var.isNamed()) throw TypeQLException.of(FILTER_VARIABLE_ANONYMOUS);
            if (!match.namedVariables().contains(var))
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE.message(var));
            if (duplicates.contains(var)) throw TypeQLException.of(ILLEGAL_FILTER_VARIABLE_REPEATING.message(var));
            else duplicates.add(var);
        }
    }

    public MatchClause match() {
        return match;
    }

    public Modifiers modifiers() {
        return modifiers;
    }

    public List<TypeQLVariable> effectiveFilter() {
        return effectiveFilter;
    }

    public List<TypeQLVariable> filter() {
        return filter;
    }

    @Override
    public Aggregate aggregate(TypeQLToken.Aggregate.Method method, TypeQLVariable var) {
        return new Aggregate(this, method, var);
    }

    public Group group(TypeQLVariable var) {
        return new Group(this, var);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.READ;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder(match.toString(pretty));
        if (pretty) query.append(NEW_LINE);
        query.append(GET);
        if (!filter.isEmpty()) {
            query.append(filter.stream().map(TypeQLVariable::toString)
                    .collect(Collectors.joining(COMMA_SPACE.toString(), SPACE.toString(), ""))
            );
        }
        query.append(SEMICOLON);
        appendModifiers(query, modifiers, pretty);
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
        return Objects.equals(this.match, that.match) && this.filter.equals(that.filter)
                && this.modifiers.equals(that.modifiers);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unmodified extends TypeQLGet implements TypeQLQuery.Unmodified<TypeQLGet, Sorted, Offset, Limited> {

        public Unmodified(MatchClause match, List<TypeQLVariable> filter) {
            super(match, filter, Modifiers.EMPTY);
        }

        @Override
        public TypeQLGet modifiers(Modifiers modifier) {
            if (modifier.sorting != null) TypeQLQuery.validateSorting(match, modifier.sorting);
            return new TypeQLGet(match, filter, modifier);
        }

        @Override
        public TypeQLGet.Sorted sort(Modifiers.Sorting sorting) {
            return new TypeQLGet.Sorted(this, sorting);
        }

        @Override
        public TypeQLGet.Offset offset(long offset) {
            return new TypeQLGet.Offset(this, offset);
        }

        @Override
        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLGet implements TypeQLQuery.Sorted<Offset, Limited> {

        public Sorted(TypeQLGet get, Modifiers.Sorting sorting) {
            super(get.match, get.filter, new Modifiers(sorting, get.modifiers.offset, get.modifiers.limit));
            TypeQLQuery.validateSorting(match, sorting);
        }

        @Override
        public TypeQLGet.Offset offset(long offset) {
            return new TypeQLGet.Offset(this, offset);
        }

        @Override
        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLGet implements TypeQLQuery.Offset<Limited> {

        public Offset(TypeQLGet get, long offset) {
            super(get.match, get.filter, new Modifiers(get.modifiers.sorting, offset, get.modifiers.limit));
        }

        @Override
        public TypeQLGet.Limited limit(long limit) {
            return new TypeQLGet.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLGet implements TypeQLQuery.Limited {

        public Limited(TypeQLGet get, long limit) {
            super(get.match, get.filter, new Modifiers(get.modifiers.sorting, get.modifiers.offset, limit));
        }
    }

    public static class Aggregate implements TypeQLQuery {

        private final TypeQLGet query;
        private final TypeQLToken.Aggregate.Method method;
        private final TypeQLVariable var;
        private final int hash;

        public Aggregate(TypeQLGet query, TypeQLToken.Aggregate.Method method, TypeQLVariable var) {
            if (query == null) throw new NullPointerException("GetQuery is null");
            if (method == null) throw new NullPointerException("Method is null");

            if (var == null && !method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                throw new NullPointerException("Variable is null");
            } else if (var != null && method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                throw TypeQLException.of(INVALID_COUNT_VARIABLE_ARGUMENT.message());
            } else if (var != null && !query.effectiveFilter().contains(var)) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE.message(var.toString()));
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

        public TypeQLGet get() {
            return query;
        }

        public TypeQLToken.Aggregate.Method method() {
            return method;
        }

        public TypeQLVariable var() {
            return var;
        }

        @Override
        public String toString() {
            return toString(true);
        }

        @Override
        public final String toString(boolean pretty) {
            StringBuilder query = new StringBuilder();
            query.append(get().toString(pretty));
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

    public static class Group implements TypeQLQuery, Aggregatable<Group.Aggregate> {

        private final TypeQLGet query;
        private final TypeQLVariable var;
        private final int hash;

        public Group(TypeQLGet query, TypeQLVariable var) {
            if (query == null) throw new NullPointerException("GetQuery is null");
            if (var == null) throw new NullPointerException("Variable is null");
            else if (!query.effectiveFilter().contains(var)) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE.message(var.toString()));
            }

            this.query = query;
            this.var = var;
            this.hash = Objects.hash(query, var);
        }

        @Override
        public TypeQLArg.QueryType type() {
            return TypeQLArg.QueryType.READ;
        }

        public TypeQLGet get() {
            return query;
        }

        public TypeQLVariable var() {
            return var;
        }

        @Override
        public Aggregate aggregate(TypeQLToken.Aggregate.Method method, TypeQLVariable var) {
            return new Aggregate(this, method, var);
        }

        @Override
        public String toString() {
            return toString(true);
        }

        @Override
        public String toString(boolean pretty) {
            if (pretty) return get().toString(pretty) + NEW_LINE + GROUP + SPACE + var + SEMICOLON;
            else return get().toString(pretty) + GROUP + SPACE + var + SEMICOLON;
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

        public static class Aggregate implements TypeQLQuery {

            private final TypeQLGet.Group group;
            private final TypeQLToken.Aggregate.Method method;
            private final TypeQLVariable var;
            private final int hash;

            public Aggregate(TypeQLGet.Group group, TypeQLToken.Aggregate.Method method, TypeQLVariable var) {
                if (group == null) throw new NullPointerException("TypeQLGet.Group is null");
                if (method == null) throw new NullPointerException("Method is null");
                if (var == null && !method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                    throw new NullPointerException("Variable is null");
                } else if (var != null && method.equals(TypeQLToken.Aggregate.Method.COUNT)) {
                    throw new IllegalArgumentException(INVALID_COUNT_VARIABLE_ARGUMENT.message());
                } else if (var != null && !group.query.effectiveFilter().contains(var)) {
                    throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE.message(var.toString()));
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

            public TypeQLVariable var() {
                return var;
            }

            @Override
            public String toString() {
                return toString(true);
            }

            @Override
            public final String toString(boolean pretty) {
                StringBuilder query = new StringBuilder();
                query.append(group().get().toString(pretty));
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
