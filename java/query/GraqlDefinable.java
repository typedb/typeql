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

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Definable;
import graql.lang.pattern.schema.Rule;
import graql.lang.pattern.variable.TypeVariable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Command.DEFINE;
import static graql.lang.common.GraqlToken.Command.UNDEFINE;
import static graql.lang.common.exception.ErrorMessage.MISSING_DEFINABLES;
import static java.util.stream.Collectors.joining;

abstract class GraqlDefinable extends GraqlQuery {

    private final GraqlToken.Command keyword;
    private final List<Definable> definables;
    private final List<TypeVariable> variables = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final int hash;

    GraqlDefinable(GraqlToken.Command keyword, List<Definable> definables) {
        assert keyword == DEFINE || keyword == UNDEFINE;
        if (definables == null || definables.isEmpty()) throw GraqlException.of(MISSING_DEFINABLES.message());
        this.definables = new ArrayList<>(definables);
        for (Definable definable : definables) {
            if (definable.isRule()) rules.add(definable.asRule());
            if (definable.isTypeVariable()) variables.add(definable.asTypeVariable());
        }
        final LinkedList<TypeVariable> typeVarsToVerify = new LinkedList<>(variables);
        while (!typeVarsToVerify.isEmpty()) {
            final TypeVariable v = typeVarsToVerify.removeFirst();
            if (!v.isLabelled()) throw GraqlException.of(ErrorMessage.INVALID_DEFINE_QUERY_VARIABLE.message());
            else v.constraints().forEach(c -> typeVarsToVerify.addAll(c.variables()));
        }

        this.keyword = keyword;
        this.hash = Objects.hash(this.keyword, this.variables, this.rules);
    }

    public final List<TypeVariable> variables() {
        return variables;
    }

    public final List<Rule> rules() {
        return rules;
    }

    @Override
    public final String toString() {
        final StringBuilder query = new StringBuilder();
        query.append(keyword);

        if (definables.size() > 1) query.append(NEW_LINE);
        else query.append(GraqlToken.Char.SPACE);

        query.append(definables.stream().map(Definable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GraqlDefinable that = (GraqlDefinable) o;
        return this.keyword.equals(that.keyword) && this.definables.equals(that.definables);
    }

    @Override
    public final int hashCode() {
        return hash;
    }
}
