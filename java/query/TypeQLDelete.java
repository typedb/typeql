/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;

import javax.annotation.Nullable;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.DELETE;
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
            if (modifier.sorting != null) TypeQLQuery.validateSorting(match, modifier.sorting);
            return new TypeQLDelete(match, statements, modifier);
        }

        @Override
        public TypeQLDelete.Sorted sort(Modifiers.Sorting sorting) {
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

        public Sorted(TypeQLDelete delete, Modifiers.Sorting sorting) {
            super(delete.match, delete.statements, new Modifiers(sorting, delete.modifiers.offset, delete.modifiers.limit));
            TypeQLQuery.validateSorting(match, sorting);
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
