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

import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.statement.Statement;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.INSERT;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static com.vaticle.typeql.lang.query.TypeQLDelete.validDeleteStatements;
import static com.vaticle.typeql.lang.query.TypeQLInsert.validInsertStatements;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TypeQLUpdate extends TypeQLWritable {

    private final List<ThingStatement<?>> deleteStatements;
    private final List<ThingStatement<?>> insertStatements;
    private final int hash;

    private List<TypeQLVariable> namedDeleteVariables;
    private List<TypeQLVariable> namedInsertVariables;
    private final Modifiers modifiers;

    public TypeQLUpdate(MatchClause match, List<ThingStatement<?>> deleteStatements,
                        List<ThingStatement<?>> insertStatements) {
        this(match, deleteStatements, insertStatements, Modifiers.EMPTY);
    }

    public TypeQLUpdate(MatchClause match, List<ThingStatement<?>> deleteStatements,
                        List<ThingStatement<?>> insertStatements, Modifiers modifiers) {
        super(match);
        this.deleteStatements = validDeleteStatements(match, deleteStatements);
        this.insertStatements = validInsertStatements(match, insertStatements);
        this.modifiers = modifiers;
        Stream<Pattern> patterns = concat(
                match.patternsRecursive(), concat(deleteStatements.stream(), insertStatements.stream())
        );
        validateNamesUnique(patterns);
        this.hash = Objects.hash(match, deleteStatements, insertStatements, modifiers);
    }

    public List<ThingStatement<?>> deleteStatements() {
        return deleteStatements;
    }

    public List<ThingStatement<?>> insertStatements() {
        return insertStatements;
    }

    public Modifiers modifiers() {
        return modifiers;
    }

    public List<TypeQLVariable> namedDeleteVariables() {
        if (namedDeleteVariables == null) {
            namedDeleteVariables = deleteStatements.stream().flatMap(Statement::variables)
                    .filter(TypeQLVariable::isNamed).distinct().collect(toList());
        }
        return namedDeleteVariables;
    }

    public List<TypeQLVariable> namedInsertVariables() {
        if (namedInsertVariables == null) {
            namedInsertVariables = insertStatements.stream().flatMap(Statement::variables)
                    .filter(TypeQLVariable::isNamed).distinct().collect(toList());
        }
        return namedInsertVariables;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder();
        query.append(match.toString(pretty)).append(NEW_LINE);
        appendClause(query, DELETE, deleteStatements.stream().map(v -> v.toString(pretty)), pretty);
        query.append(NEW_LINE);
        appendClause(query, INSERT, insertStatements.stream().map(v -> v.toString(pretty)), pretty);
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
        return (this.match.equals(that.match) && this.deleteStatements.equals(that.deleteStatements) &&
                this.insertStatements.equals(that.insertStatements)) && this.modifiers.equals(that.modifiers);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unmodified extends TypeQLUpdate implements TypeQLQuery.Unmodified<TypeQLUpdate, TypeQLUpdate.Sorted, TypeQLUpdate.Offset, TypeQLUpdate.Limited> {

        public Unmodified(MatchClause match, List<ThingStatement<?>> deleteStatements, List<ThingStatement<?>> insertStatements) {
            super(match, deleteStatements, insertStatements, Modifiers.EMPTY);
        }

        @Override
        public TypeQLUpdate modifiers(Modifiers modifier) {
            if (modifier.sorting != null) TypeQLQuery.validateSorting(match, modifier.sorting);
            return new TypeQLUpdate(match, deleteStatements(), insertStatements(), modifier);
        }

        @Override
        public TypeQLUpdate.Sorted sort(Modifiers.Sorting sorting) {
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

        public Sorted(TypeQLUpdate delete, Modifiers.Sorting sorting) {
            super(delete.match, delete.deleteStatements, delete.insertStatements, new Modifiers(sorting, delete.modifiers.offset, delete.modifiers.limit));
            TypeQLQuery.validateSorting(match, sorting);
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
            super(delete.match, delete.deleteStatements, delete.insertStatements, new Modifiers(delete.modifiers.sorting, offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLUpdate.Limited limit(long limit) {
            return new TypeQLUpdate.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLUpdate implements TypeQLQuery.Limited {

        public Limited(TypeQLUpdate delete, long limit) {
            super(delete.match, delete.deleteStatements, delete.insertStatements, new Modifiers(delete.modifiers.sorting, delete.modifiers.offset, limit));
        }
    }
}
