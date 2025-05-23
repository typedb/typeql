/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.query;

import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.pattern.statement.ThingStatement;

import java.util.List;

import static com.typeql.lang.common.TypeQLToken.Clause.INSERT;
import static com.typeql.lang.common.exception.ErrorMessage.NO_VARIABLE_IN_SCOPE_INSERT;
import static com.typeql.lang.pattern.Pattern.validateNamesUnique;
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
            if (modifier.sorting != null) TypeQLQuery.validateSorting(match, modifier.sorting);
            return new TypeQLInsert(match, statements, modifier);
        }

        @Override
        public TypeQLInsert.Sorted sort(Modifiers.Sorting sorting) {
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

        public Sorted(TypeQLInsert insert, Modifiers.Sorting sorting) {
            super(insert.match, insert.statements, new Modifiers(sorting, insert.modifiers.offset, insert.modifiers.limit));
            TypeQLQuery.validateSorting(match, sorting);
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
