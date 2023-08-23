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

import javax.annotation.Nullable;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_DELETE;
import static java.util.Objects.requireNonNull;

public class TypeQLDelete extends TypeQLWritable.InsertOrDelete {

    TypeQLDelete(MatchClause match, List<ThingStatement<?>> statements) {
        this(match, statements, Modifiers.EMPTY);
    }

    TypeQLDelete(MatchClause match, List<ThingStatement<?>> statements, @Nullable Modifiers modifiers) {
        super(DELETE, requireNonNull(match), validDeleteStatements(match, statements), modifiers == null ? Modifiers.EMPTY : modifiers);
    }

    static List<ThingStatement<?>> validDeleteStatements(MatchClause match, List<ThingStatement<?>> statements) {
        statements.forEach(stmt -> {
            stmt.variables().forEach(var -> {
                if (var.isNamed() && !match.namedVariables().contains(var)) {
                    throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(var.reference()));
                }
            });
        });
        return statements;
    }

    public static class Unmodified extends TypeQLDelete implements TypeQLQuery.Unmodified<TypeQLDelete, TypeQLDelete.Sorted, TypeQLDelete.Offset, TypeQLDelete.Limited> {

        public Unmodified(MatchClause match, List<ThingStatement<?>> statements) {
            super(match, statements, Modifiers.EMPTY);
        }

        public TypeQLUpdate.Unmodified insert(ThingStatement<?>... statements) {
            return insert(list(statements));
        }

        public TypeQLUpdate.Unmodified insert(List<ThingStatement<?>> statements) {
            return new TypeQLUpdate.Unmodified(this.match().get(), this.statements, statements);
        }

        @Override
        public TypeQLDelete modifiers(Modifiers modifier) {
            return new TypeQLDelete(match, statements, modifier);
        }

        @Override
        public TypeQLDelete.Sorted sort(Sortable.Sorting sorting) {
            return new TypeQLDelete.Sorted(this, sorting);
        }

        @Override
        public TypeQLDelete.Offset offset(long offset) {
            return new TypeQLDelete.Offset(this, offset);
        }

        @Override
        public TypeQLDelete.Limited limit(long limit) {
            return new TypeQLDelete.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLDelete implements TypeQLQuery.Sorted<TypeQLDelete.Offset, TypeQLDelete.Limited> {

        public Sorted(TypeQLDelete delete, Sortable.Sorting sorting) {
            super(delete.match, delete.statements, new Modifiers(sorting, delete.modifiers.offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLDelete.Offset offset(long offset) {
            return new TypeQLDelete.Offset(this, offset);
        }

        @Override
        public TypeQLDelete.Limited limit(long limit) {
            return new TypeQLDelete.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLDelete implements TypeQLQuery.Offset<TypeQLDelete.Limited> {

        public Offset(TypeQLDelete delete, long offset) {
            super(delete.match, delete.statements, new Modifiers(delete.modifiers.sorting, offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLDelete.Limited limit(long limit) {
            return new TypeQLDelete.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLDelete implements TypeQLQuery.Limited {

        public Limited(TypeQLDelete delete, long limit) {
            super(delete.match, delete.statements, new Modifiers(delete.modifiers.sorting, delete.modifiers.offset, limit));
        }
    }
}
