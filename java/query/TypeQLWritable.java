/*
 * Copyright (C) 2021 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public abstract class TypeQLWritable extends TypeQLQuery {

    protected final TypeQLMatch.Unfiltered match;

    TypeQLWritable(@Nullable TypeQLMatch.Unfiltered match) {
        this.match = match;
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    abstract static class InsertOrDelete extends TypeQLWritable {

        private List<UnboundVariable> namedVariablesUnbound;
        private final TypeQLToken.Command keyword;
        protected final List<ThingVariable<?>> variables;
        private final int hash;

        InsertOrDelete(TypeQLToken.Command keyword, @Nullable TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
            super(match);
            assert keyword == INSERT || keyword == DELETE;
            if (variables == null || variables.isEmpty()) throw TypeQLException.of(MISSING_PATTERNS.message());
            this.keyword = keyword;
            this.variables = variables;
            this.hash = Objects.hash(this.keyword, this.match, this.variables);
        }

        public List<UnboundVariable> namedVariablesUnbound() {
            if (namedVariablesUnbound == null) {
                namedVariablesUnbound = variables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                        .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
            }
            return namedVariablesUnbound;
        }

        @Override
        public String toString() {
            StringBuilder query = new StringBuilder();

            if (match != null) query.append(match).append(NEW_LINE);
            query.append(keyword);

            if (variables.size() > 1) query.append(NEW_LINE);
            else query.append(SPACE);

            query.append(variables.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
            query.append(SEMICOLON);
            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InsertOrDelete that = (InsertOrDelete) o;
            return (this.keyword.equals(that.keyword) &&
                    Objects.equals(this.match, that.match) &&
                    this.variables.equals(that.variables));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
