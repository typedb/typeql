/*
 * Copyright (C) 2021 Grakn Labs
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

package graql.lang.query;

import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.pattern.variable.Variable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Command.DELETE;
import static graql.lang.common.GraqlToken.Command.INSERT;
import static graql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public abstract class GraqlWritable extends GraqlQuery {

    protected final GraqlMatch.Unfiltered match;
    protected final List<ThingVariable<?>> variables;

    private List<UnboundVariable> namedVariablesUnbound;

    GraqlWritable(@Nullable GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        this.match = match;
        this.variables = list(variables);
    }

    @Override
    public GraqlArg.QueryType type() {
        return GraqlArg.QueryType.WRITE;
    }

    public List<UnboundVariable> namedVariablesUnbound() {
        if (namedVariablesUnbound == null) {
            namedVariablesUnbound = variables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedVariablesUnbound;
    }

    abstract static class InsertOrDelete extends GraqlWritable {

        private final GraqlToken.Command keyword;
        private final int hash;

        InsertOrDelete(GraqlToken.Command keyword, @Nullable GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
            super(match, variables);
            assert keyword == INSERT || keyword == DELETE;
            if (variables == null || variables.isEmpty()) throw GraqlException.of(MISSING_PATTERNS.message());
            this.keyword = keyword;
            this.hash = Objects.hash(this.keyword, this.match, this.variables);
        }

        @Override
        public String toString() {
            final StringBuilder query = new StringBuilder();

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
            final InsertOrDelete that = (InsertOrDelete) o;
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
