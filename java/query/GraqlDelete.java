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
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;

import java.util.List;
import java.util.Objects;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.exception.ErrorMessage.INVALID_VARIABLE_OUT_OF_SCOPE;
import static java.util.stream.Collectors.joining;

public class GraqlDelete extends GraqlQuery {

    private final MatchClause match;
    private final List<ThingVariable<?>> variables;
    private final int hash;

    GraqlDelete(MatchClause match, List<ThingVariable<?>> variables) {
        if (match == null) throw new NullPointerException("Null match");
        if (variables == null || variables.isEmpty())
            throw GraqlException.create(ErrorMessage.MISSING_PATTERNS.message());

        variables.forEach(var -> {
            if (var.isNamed() && !match.variablesNamedUnbound().contains(var.withoutProperties())) {
                throw GraqlException.create(INVALID_VARIABLE_OUT_OF_SCOPE.message(var.withoutProperties().toString()));
            }
            var.variables().forEach(nestedVar -> {
                if (nestedVar.isNamed() && !match.variablesNamedUnbound().contains(nestedVar.withoutProperties())) {
                    throw GraqlException.create(INVALID_VARIABLE_OUT_OF_SCOPE.message(nestedVar.withoutProperties().toString()));
                }
            });
        });

        this.match = match;
        this.variables = list(variables);
        this.hash = Objects.hash(this.match, this.variables);
    }

    public MatchClause match() {
        return match;
    }

    public List<ThingVariable<?>> variables() {
        return variables;
    }

    public List<ThingVariable<?>> asGraph() {
        return BoundVariable.asGraph(variables);
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder(match().toString());
        query.append(NEW_LINE).append(GraqlToken.Command.DELETE);

        if (variables.size() > 1) query.append(NEW_LINE);
        else query.append(GraqlToken.Char.SPACE);

        query.append(variables.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraqlDelete that = (GraqlDelete) o;
        return (this.match.equals(that.match) &&
                this.variables.equals(that.variables));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
