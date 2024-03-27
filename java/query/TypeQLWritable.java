/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.statement.Statement;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;
import static java.util.stream.Collectors.toList;

public abstract class TypeQLWritable implements TypeQLQuery {

    protected final MatchClause match;

    TypeQLWritable(@Nullable MatchClause match) {
        this.match = match;
    }

    public Optional<MatchClause> match() {
        return Optional.ofNullable(match);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    abstract static class InsertOrDelete extends TypeQLWritable {

        private List<TypeQLVariable> namedVariables;
        private final TypeQLToken.Clause clause;
        final List<ThingStatement<?>> statements;
        final Modifiers modifiers;
        private final int hash;

        InsertOrDelete(TypeQLToken.Clause clause, @Nullable MatchClause match, List<ThingStatement<?>> statements, Modifiers modifiers) {
            super(match);
            assert clause == INSERT || clause == DELETE;
            assert modifiers != null;
            if (statements == null || statements.isEmpty()) throw TypeQLException.of(MISSING_PATTERNS.message());
            this.clause = clause;
            this.statements = statements;
            this.modifiers = modifiers;
            this.hash = Objects.hash(this.clause, this.match, this.statements, this.modifiers);
        }

        public List<TypeQLVariable> namedVariables() {
            if (namedVariables == null) {
                namedVariables = statements.stream().flatMap(Statement::variables)
                        .filter(TypeQLVariable::isNamed).distinct().collect(toList());
            }
            return namedVariables;
        }

        public Modifiers modifiers() {
            return modifiers;
        }

        public List<ThingStatement<?>> statements() {
            return statements;
        }

        @Override
        public String toString(boolean pretty) {
            StringBuilder query = new StringBuilder();
            if (match != null) query.append(match.toString(pretty)).append(NEW_LINE);
            appendClause(query, clause, statements.stream().map(v -> v.toString(pretty)), pretty);
            appendModifiers(query, modifiers, pretty);
            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
                return false;
            }
            InsertOrDelete that = (InsertOrDelete) o;
            return (this.clause.equals(that.clause) &&
                    Objects.equals(this.match, that.match) &&
                    this.statements.equals(that.statements)) &&
                    this.modifiers.equals(that.modifiers);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
