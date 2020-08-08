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

import graql.lang.Graql;
import graql.lang.common.exception.GraqlException;
import graql.lang.variable.ThingVariable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static grakn.common.collection.Collections.list;
import static graql.lang.Graql.Token.Char.NEW_LINE;
import static graql.lang.Graql.Token.Char.SEMICOLON;
import static java.util.stream.Collectors.joining;

public class GraqlInsert extends GraqlQuery {

    private final MatchClause match;
    private final List<ThingVariable<?>> variables;
    private final int hash;

    public GraqlInsert(List<ThingVariable<?>> variables) {
        this(null, variables);
    }

    GraqlInsert(@Nullable MatchClause match, List<ThingVariable<?>> variables) {
        if (variables == null || variables.isEmpty()) throw GraqlException.noPatterns();
        this.match = match;
        this.variables = list(variables);
        this.hash = Objects.hash(this.match, this.variables);
    }

    @Nullable
    public MatchClause match() {
        return match;
    }

    public List<ThingVariable<?>> variables() {
        return variables;
    }

    @Override
    public final String toString() {
        StringBuilder query = new StringBuilder();

        if (match() != null) query.append(match()).append(NEW_LINE);
        query.append(Graql.Token.Command.INSERT);

        if (variables.size() > 1) query.append(NEW_LINE);
        else query.append(Graql.Token.Char.SPACE);

        query.append(variables().stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraqlInsert that = (GraqlInsert) o;
        return (Objects.equals(this.match, that.match) &&
                this.variables.equals(that.variables));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
