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

import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;
import com.vaticle.typeql.lang.query.builder.Sortable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static com.vaticle.typeql.lang.query.TypeQLDelete.validDeleteVars;
import static com.vaticle.typeql.lang.query.TypeQLInsert.validInsertVars;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TypeQLUpdate extends TypeQLWritable {

    private final List<ThingVariable<?>> deleteVariables;
    private final List<ThingVariable<?>> insertVariables;
    private final int hash;

    private List<UnboundVariable> namedDeleteVariablesUnbound;
    private List<UnboundVariable> namedInsertVariablesUnbound;
    private final Modifiers modifiers;

    public TypeQLUpdate(MatchClause match, List<ThingVariable<?>> deleteVariables,
                        List<ThingVariable<?>> insertVariables) {
        this(match, deleteVariables, insertVariables, Modifiers.EMPTY);
    }

    public TypeQLUpdate(MatchClause match, List<ThingVariable<?>> deleteVariables,
                        List<ThingVariable<?>> insertVariables, Modifiers modifiers) {
        super(match);
        this.deleteVariables = validDeleteVars(match, deleteVariables);
        this.insertVariables = validInsertVars(match, insertVariables);
        this.modifiers = modifiers;
        Stream<Pattern> patterns = concat(
                match.patternsRecursive(), concat(deleteVariables.stream(), insertVariables.stream())
        );
        validateNamesUnique(patterns);
        this.hash = Objects.hash(match, deleteVariables, insertVariables, modifiers);
    }

    public List<ThingVariable<?>> deleteVariables() {
        return deleteVariables;
    }

    public List<ThingVariable<?>> insertVariables() {
        return insertVariables;
    }

    public Modifiers modifiers() {
        return modifiers;
    }

    public List<UnboundVariable> namedDeleteVariablesUnbound() {
        if (namedDeleteVariablesUnbound == null) {
            namedDeleteVariablesUnbound = deleteVariables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamedConcept).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedDeleteVariablesUnbound;
    }

    public List<UnboundVariable> namedInsertVariablesUnbound() {
        if (namedInsertVariablesUnbound == null) {
            namedInsertVariablesUnbound = insertVariables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedInsertVariablesUnbound;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder();
        query.append(match.toString(pretty)).append(NEW_LINE);
        appendClause(query, DELETE, deleteVariables.stream().map(v -> v.toString(pretty)), pretty);
        query.append(NEW_LINE);
        appendClause(query, INSERT, insertVariables.stream().map(v -> v.toString(pretty)), pretty);
        appendModifiers(query, modifiers, pretty);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }
        TypeQLUpdate that = (TypeQLUpdate) o;
        return (this.match.equals(that.match) && this.deleteVariables.equals(that.deleteVariables) &&
                this.insertVariables.equals(that.insertVariables)) && this.modifiers.equals(that.modifiers);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unmodified extends TypeQLUpdate implements TypeQLQuery.Unmodified<TypeQLUpdate, TypeQLUpdate.Sorted, TypeQLUpdate.Offset, TypeQLUpdate.Limited> {

        public Unmodified(MatchClause match, List<ThingVariable<?>> deleteVariables, List<ThingVariable<?>> insertVariables) {
            super(match, deleteVariables, insertVariables, Modifiers.EMPTY);
        }

        @Override
        public TypeQLUpdate modifier(Modifiers modifier) {
            return new TypeQLUpdate(match, deleteVariables(), insertVariables(), modifier);
        }

        @Override
        public TypeQLUpdate.Sorted sort(Sortable.Sorting sorting) {
            return new TypeQLUpdate.Sorted(this, sorting);
        }

        @Override
        public TypeQLUpdate.Offset offset(long offset) {
            return new TypeQLUpdate.Offset(this, offset);
        }

        @Override
        public TypeQLUpdate.Limited limit(long limit) {
            return new TypeQLUpdate.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLUpdate implements TypeQLQuery.Sorted<TypeQLUpdate.Offset, TypeQLUpdate.Limited> {

        public Sorted(TypeQLUpdate delete, Sortable.Sorting sorting) {
            super(delete.match, delete.deleteVariables, delete.insertVariables, new Modifiers(sorting, delete.modifiers.offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLUpdate.Offset offset(long offset) {
            return new TypeQLUpdate.Offset(this, offset);
        }

        @Override
        public TypeQLUpdate.Limited limit(long limit) {
            return new TypeQLUpdate.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLUpdate implements TypeQLQuery.Offset<TypeQLUpdate.Limited> {

        public Offset(TypeQLUpdate delete, long offset) {
            super(delete.match, delete.deleteVariables, delete.insertVariables, new Modifiers(delete.modifiers.sorting, offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLUpdate.Limited limit(long limit) {
            return new TypeQLUpdate.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLUpdate implements TypeQLQuery.Limited {

        public Limited(TypeQLUpdate delete, long limit) {
            super(delete.match, delete.deleteVariables, delete.insertVariables, new Modifiers(delete.modifiers.sorting, delete.modifiers.offset, limit));
        }
    }
}
