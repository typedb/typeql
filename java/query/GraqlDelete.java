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
import graql.lang.exception.GraqlException;
import graql.lang.variable.ThingVariable;

import java.util.List;
import java.util.Objects;

import static graql.lang.Graql.Token.Char.NEW_LINE;
import static graql.lang.Graql.Token.Char.SEMICOLON;
import static java.util.stream.Collectors.joining;

public class GraqlDelete extends GraqlQuery {

    private final MatchClause match;
    private final List<ThingVariable> variable;
    private final int hash;

    public GraqlDelete(MatchClause match, List<ThingVariable> variable) {
        if (match == null) throw new NullPointerException("Null match");
        if (variable == null || variable.isEmpty()) throw GraqlException.noPatterns();
        this.match = match;
        this.variable = variable;
        this.hash = Objects.hash(this.match, this.variable);
    }

    public MatchClause match() {
        return match;
    }

    public List<ThingVariable> variables() {
        return variable;
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder(match().toString());
        query.append(NEW_LINE).append(Graql.Token.Command.DELETE);

        if (variable.size() > 1) query.append(NEW_LINE);
        else query.append(Graql.Token.Char.SPACE);

        query.append(variable.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraqlDelete that = (GraqlDelete) o;
        return (this.match.equals(that.match) &&
                this.variable.equals(that.variable));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
