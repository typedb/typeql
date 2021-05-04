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

import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.query.TypeQLDelete.validDeleteVars;
import static com.vaticle.typeql.lang.query.TypeQLInsert.validInsertVars;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TypeQLUpdate extends TypeQLWritable {

    private final List<ThingVariable<?>> deleteVariables;
    private final List<ThingVariable<?>> insertVariables;
    private final int hash;

    private List<UnboundVariable> namedDeleteVariablesUnbound;
    private List<UnboundVariable> namedInsertVariablesUnbound;

    public TypeQLUpdate(TypeQLMatch.Unfiltered match, List<ThingVariable<?>> deleteVariables,
                        List<ThingVariable<?>> insertVariables) {
        super(match);
        this.deleteVariables = validDeleteVars(match, deleteVariables);
        this.insertVariables = validInsertVars(match, insertVariables);
        this.hash = Objects.hash(match, deleteVariables, insertVariables);
    }

    public TypeQLMatch.Unfiltered match() {
        assert match != null;
        return match;
    }


    public List<ThingVariable<?>> deleteVariables() {
        return deleteVariables;
    }

    public List<ThingVariable<?>> insertVariables() {
        return insertVariables;
    }

    public List<UnboundVariable> namedDeleteVariablesUnbound() {
        if (namedDeleteVariablesUnbound == null) {
            namedDeleteVariablesUnbound = deleteVariables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedDeleteVariablesUnbound;
    }

    public List<UnboundVariable> namedInsertVariablesUnbound() {
        if (namedInsertVariablesUnbound == null) {
            namedInsertVariablesUnbound = insertVariables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedInsertVariablesUnbound;
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder();
        query.append(match).append(NEW_LINE);

        query.append(DELETE);
        if (deleteVariables.size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);
        query.append(deleteVariables.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON).append(NEW_LINE);

        query.append(INSERT);
        if (insertVariables.size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);
        query.append(insertVariables.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeQLUpdate that = (TypeQLUpdate) o;
        return (this.match.equals(that.match) &&
                this.deleteVariables.equals(that.deleteVariables) &&
                this.insertVariables.equals(that.insertVariables));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
