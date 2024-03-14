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

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.statement.Statement;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.MATCH;
import static com.vaticle.typeql.lang.common.TypeQLToken.Modifier.LIMIT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Modifier.OFFSET;
import static com.vaticle.typeql.lang.common.TypeQLToken.Modifier.SORT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_SORTING_VARIABLE_NOT_MATCHED;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_PATTERN_STATEMENT_HAS_NO_NAMED_VARIABLE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_NOT_SORTED;
import static java.util.Collections.emptyList;
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

    default TypeQLGet asGet() {
        if (this instanceof TypeQLGet) {
            return (TypeQLGet) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.class)));
        }
    }

    default TypeQLGet.Aggregate asGetAggregate() {
        if (this instanceof TypeQLGet.Aggregate) {
            return (TypeQLGet.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Aggregate.class)));
        }
    }

    default TypeQLGet.Group asGetGroup() {
        if (this instanceof TypeQLGet.Group) {
            return (TypeQLGet.Group) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Group.class)));
        }
    }

    default TypeQLGet.Group.Aggregate asGetGroupAggregate() {
        if (this instanceof TypeQLGet.Group.Aggregate) {
            return (TypeQLGet.Group.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLGet.Group.Aggregate.class)));
        }
    }

    default TypeQLFetch asFetch() {
        if (this instanceof TypeQLFetch) {
            return (TypeQLFetch) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLFetch.class)));
        }
    }

    static void appendClause(StringBuilder builder, TypeQLToken.Clause clause, Stream<String> elements, boolean pretty) {
        builder.append(clause).append(NEW_LINE);
        if (pretty) builder.append(elements.collect(SEMICOLON_NEW_LINE.joiner()));
        else builder.append(elements.collect(SEMICOLON_SPACE.joiner()));
        builder.append(SEMICOLON);
    }

    static void appendModifiers(StringBuilder builder, Modifiers modifiers, boolean pretty) {
        if (!modifiers.isEmpty()) {
            if (pretty) builder.append(NEW_LINE);
            builder.append(modifiers);
        }
    }

    String toString(boolean pretty);

    static void validateSorting(MatchClause matchClause, Modifiers.Sorting sorting) {
        for (TypeQLVariable variable : sorting.variables()) {
            if (!matchClause.namedVariables().contains(variable)) {
                throw TypeQLException.of(INVALID_SORTING_VARIABLE_NOT_MATCHED.message(variable));
            }
        }
    }

    interface Unmodified<Q, S extends Sorted<O, L>, O extends Offset<L>, L extends Limited>
            extends TypeQLQuery {

        Q modifiers(Modifiers modifier);

        default S sort(TypeQLVariable var, TypeQLVariable... vars) {
            List<Pair<TypeQLVariable, TypeQLArg.Order>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(var, null));
            for (TypeQLVariable v : vars) pairs.add(new Pair<>(v, null));
            return sort(pairs);
        }

        default S sort(Pair<TypeQLVariable, String> varOrder1) {
            return sort(list(parseVarOrder(varOrder1)));
        }

        default S sort(Pair<TypeQLVariable, String> varOrder1, Pair<TypeQLVariable, String> varOrder2) {
            return sort(list(parseVarOrder(varOrder1), parseVarOrder(varOrder2)));
        }

        default S sort(Pair<TypeQLVariable, String> varOrder1, Pair<TypeQLVariable, String> varOrder2,
                       Pair<TypeQLVariable, String> varOrder3) {
            return sort(list(parseVarOrder(varOrder1), parseVarOrder(varOrder2), parseVarOrder(varOrder3)));
        }

        static Pair<TypeQLVariable, TypeQLArg.Order> parseVarOrder(Pair<TypeQLVariable, String> varOrder) {
            return new Pair<>(varOrder.first(), TypeQLArg.Order.of(varOrder.second()));
        }

        default S sort(List<Pair<TypeQLVariable, TypeQLArg.Order>> varOrders) {
            return sort(Modifiers.Sorting.create(varOrders));
        }

        S sort(Modifiers.Sorting sorting);

        O offset(long offset);

        L limit(long limit);
    }

    interface Sorted<O extends Offset<L>, L extends Limited> extends TypeQLQuery {

        Modifiers modifiers();

        O offset(long offset);

        L limit(long limit);
    }

    interface Offset<L extends Limited> extends TypeQLQuery {

        Modifiers modifiers();

        L limit(long offset);
    }

    interface Limited extends TypeQLQuery {

        Modifiers modifiers();
    }

    class MatchClause {

        private final Conjunction<? extends Pattern> conjunction;

        private List<Statement> statements;
        private Set<TypeQLVariable> variablesNamed;

        public MatchClause(Conjunction<? extends Pattern> conjunction) {
            this.conjunction = conjunction;

            eachPatternStatementHasNamedVariable(conjunction.patterns());
            nestedPatternsAreBounded();
        }

        private void nestedPatternsAreBounded() {
            conjunction.patterns().stream().filter(pattern -> !pattern.isStatement())
                    .forEach(pattern -> pattern.validateIsBoundedBy(namedVariables()));
        }

        private void eachPatternStatementHasNamedVariable(List<? extends Pattern> patterns) {
            patterns.forEach(pattern -> {
                if (pattern.isStatement() && pattern.asStatement().variables().noneMatch(TypeQLVariable::isNamed)) {
                    throw TypeQLException.of(MATCH_PATTERN_STATEMENT_HAS_NO_NAMED_VARIABLE.message(pattern));
                } else if (!pattern.isStatement()) {
                    eachPatternStatementHasNamedVariable(pattern.patterns());
                }
            });
        }

        public TypeQLGet.Unmodified get(TypeQLVariable... vars) {
            if (vars.length == 0) return get(emptyList());
            else return get(list(vars));
        }

        public TypeQLGet.Unmodified get(List<TypeQLVariable> vars) {
            return new TypeQLGet.Unmodified(this, vars);
        }

        public TypeQLFetch.Unmodified fetch(TypeQLFetch.Projection... projections) {
            return new TypeQLFetch.Unmodified(this, list(projections));
        }

        public TypeQLFetch.Unmodified fetch(List<TypeQLFetch.Projection> projections) {
            return new TypeQLFetch.Unmodified(this, projections);
        }

        public TypeQLInsert.Unmodified insert(ThingStatement<?>... things) {
            return insert(list(things));
        }

        public TypeQLInsert.Unmodified insert(List<ThingStatement<?>> things) {
            return new TypeQLInsert.Unmodified(this, things);
        }

        public TypeQLDelete.Unmodified delete(ThingStatement<?>... things) {
            return delete(list(things));
        }

        public TypeQLDelete.Unmodified delete(List<ThingStatement<?>> things) {
            return new TypeQLDelete.Unmodified(this, things);
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

        public List<Statement> statements() {
            if (statements == null) statements = conjunction.statements().collect(toList());
            return statements;
        }

        public Set<TypeQLVariable> namedVariables() {
            if (variablesNamed == null) {
                variablesNamed = conjunction.namedVariables().collect(toSet());
            }
            return variablesNamed;
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

        public static Modifiers EMPTY = new Modifiers(null, null, null);

        final Sorting sorting;
        final Long offset;
        final Long limit;

        private final int hash;

        public Modifiers(@Nullable Sorting sorting, @Nullable Long offset, @Nullable Long limit) {
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

        public Optional<Sorting> sort() {
            return Optional.ofNullable(sorting);
        }

        public boolean isEmpty() {
            return sorting == null && offset == null && limit == null;
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

        public static class Sorting {

            private final int hash;
            private final List<TypeQLVariable> variables;
            private final Map<TypeQLVariable, TypeQLArg.Order> orders;

            private Sorting(List<TypeQLVariable> variables, Map<TypeQLVariable, TypeQLArg.Order> orders) {
                this.variables = variables;
                this.orders = orders;
                this.hash = Objects.hash(variables, orders);
            }

            public static Sorting create(Pair<TypeQLVariable, TypeQLArg.Order> sorting) {
                return create(list(sorting));
            }

            public static Sorting create(List<Pair<TypeQLVariable, TypeQLArg.Order>> sorting) {
                List<TypeQLVariable> vars = new ArrayList<>();
                Map<TypeQLVariable, TypeQLArg.Order> orders = new HashMap<>();
                sorting.forEach(pair -> {
                    vars.add(pair.first());
                    orders.put(pair.first(), pair.second());
                });
                return new Sorting(vars, orders);
            }

            public List<TypeQLVariable> variables() {
                return variables;
            }

            public TypeQLArg.Order getOrder(TypeQLVariable var) {
                if (!variables.contains(var)) throw TypeQLException.of(VARIABLE_NOT_SORTED.message(var));
                TypeQLArg.Order order = orders.get(var);
                return order == null ? TypeQLArg.Order.ASC : order;
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
}
