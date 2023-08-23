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

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;
import com.vaticle.typeql.lang.query.builder.Sortable;

import java.util.List;

import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.NO_VARIABLE_IN_SCOPE_INSERT;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static java.util.stream.Stream.concat;

public class TypeQLInsert extends TypeQLWritable.InsertOrDelete {

    public TypeQLInsert(List<ThingStatement<?>> statements) {
        super(INSERT, null, statements, Modifiers.EMPTY);
        validateNamesUnique(statements.stream());
    }

    public TypeQLInsert(MatchClause match, List<ThingStatement<?>> statements) {
        this(match, statements, Modifiers.EMPTY);
    }

    public TypeQLInsert(MatchClause match, List<ThingStatement<?>> statements, Modifiers modifiers) {
        super(INSERT, match, validInsertStatements(match, statements), modifiers);
        validateNamesUnique(concat(match.patternsRecursive(), statements.stream()));
    }

    static List<ThingStatement<?>> validInsertStatements(MatchClause match, List<ThingStatement<?>> statements) {
        if (statements.stream().flatMap(ThingStatement::variables).noneMatch(var -> var.isNamed() && match.namedVariables().contains(var))) {
            throw TypeQLException.of(NO_VARIABLE_IN_SCOPE_INSERT.message(statements, match.namedVariables()));
        }
        return statements;
    }

    public static class Unmodified extends TypeQLInsert implements TypeQLQuery.Unmodified<TypeQLInsert, TypeQLInsert.Sorted, TypeQLInsert.Offset, TypeQLInsert.Limited> {

        public Unmodified(MatchClause match, List<ThingStatement<?>> statements) {
            super(match, statements, Modifiers.EMPTY);
        }

        @Override
        public TypeQLInsert modifiers(Modifiers modifier) {
            return new TypeQLInsert(match, statements, modifier);
        }

        @Override
        public TypeQLInsert.Sorted sort(Sortable.Sorting sorting) {
            return new TypeQLInsert.Sorted(this, sorting);
        }

        @Override
        public TypeQLInsert.Offset offset(long offset) {
            return new TypeQLInsert.Offset(this, offset);
        }

        @Override
        public TypeQLInsert.Limited limit(long limit) {
            return new TypeQLInsert.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLInsert implements TypeQLQuery.Sorted<TypeQLInsert.Offset, TypeQLInsert.Limited> {

        public Sorted(TypeQLInsert insert, Sortable.Sorting sorting) {
            super(insert.match, insert.statements, new Modifiers(sorting, insert.modifiers.offset, insert.modifiers.limit));
        }

        @Override
        public TypeQLInsert.Offset offset(long offset) {
            return new TypeQLInsert.Offset(this, offset);
        }

        @Override
        public TypeQLInsert.Limited limit(long limit) {
            return new TypeQLInsert.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLInsert implements TypeQLQuery.Offset<TypeQLInsert.Limited> {

        public Offset(TypeQLInsert insert, long offset) {
            super(insert.match, insert.statements, new Modifiers(insert.modifiers.sorting, offset, insert.modifiers.limit));
        }

        @Override
        public TypeQLInsert.Limited limit(long limit) {
            return new TypeQLInsert.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLInsert implements TypeQLQuery.Limited {

        public Limited(TypeQLInsert insert, long limit) {
            super(insert.match, insert.statements, new Modifiers(insert.modifiers.sorting, insert.modifiers.offset, limit));
        }
    }
}
