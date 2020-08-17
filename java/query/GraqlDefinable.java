/*
 * Copyright (C) 2020 Grakn Labs
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

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.property.TypeProperty;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.TypeVariable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Command.DEFINE;
import static graql.lang.common.GraqlToken.Command.UNDEFINE;
import static graql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

abstract class GraqlDefinable extends GraqlQuery {
    private List<TypeVariable> graph;
    private final GraqlToken.Command keyword;
    private final List<TypeVariable> variables;
    private final int hash;

    GraqlDefinable(GraqlToken.Command keyword, List<TypeVariable> variables) {
        assert keyword == DEFINE || keyword == UNDEFINE;
        if (variables == null || variables.isEmpty()) throw GraqlException.create(MISSING_PATTERNS.message());
        LinkedList<TypeVariable> list = new LinkedList<>(variables);
        while (!list.isEmpty()) {
            TypeVariable v = list.removeFirst();
            if (!v.isLabelled()) throw GraqlException.create(ErrorMessage.INVALID_DEFINE_QUERY_VARIABLE.message());
            else list.addAll(v.properties().stream().flatMap(TypeProperty::variables).collect(toSet()));
        }

        this.keyword = keyword;
        this.variables = variables;
        this.hash = Objects.hash(this.keyword, this.variables);
    }

    public final List<TypeVariable> variables() {
        return variables;
    }

    public final List<TypeVariable> asGraph() {
        if (graph == null) graph = BoundVariable.asTypeGraph(variables);
        return graph;
    }

    @Override
    public final String toString() {
        StringBuilder query = new StringBuilder();
        query.append(keyword);

        if (variables.size() > 1) query.append(NEW_LINE);
        else query.append(GraqlToken.Char.SPACE);

        query.append(variables().stream().map(TypeVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraqlDefinable that = (GraqlDefinable) o;
        return this.keyword.equals(that.keyword) && this.variables.equals(that.variables);
    }

    @Override
    public final int hashCode() {
        return hash;
    }
}
