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

import graql.lang.pattern.variable.ThingVariable;

import java.util.List;
import java.util.Objects;

import static grakn.common.collection.Collections.list;
import static grakn.common.collection.Collections.set;
import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Command.DELETE;
import static graql.lang.common.GraqlToken.Command.INSERT;
import static graql.lang.query.GraqlDelete.validDeleteVars;
import static graql.lang.query.GraqlInsert.validInsertVars;
import static java.util.stream.Collectors.joining;

public class GraqlUpdate extends GraqlWritable {

    private final List<ThingVariable<?>> deleteThings;
    private final List<ThingVariable<?>> insertThings;
    private final int hash;

    public GraqlUpdate(GraqlMatch.Unfiltered match, List<ThingVariable<?>> deleteThings,
                       List<ThingVariable<?>> insertThings) {
        super(match, list(set(validDeleteVars(match, deleteThings), validInsertVars(match, insertThings))));
        this.deleteThings = deleteThings;
        this.insertThings = insertThings;
        this.hash = Objects.hash(match, deleteThings, insertThings);
    }

    public GraqlMatch.Unfiltered match() {
        assert match != null;
        return match;
    }


    public List<ThingVariable<?>> deleteVariables() {
        return deleteThings;
    }

    public List<ThingVariable<?>> insertVariables() {
        return insertThings;
    }

    @Override
    public String toString() {
        final StringBuilder query = new StringBuilder();
        query.append(match).append(NEW_LINE);

        query.append(DELETE);
        if (deleteThings.size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);
        query.append(deleteThings.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON).append(NEW_LINE);

        query.append(INSERT);
        if (insertThings.size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);
        query.append(insertThings.stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraqlUpdate that = (GraqlUpdate) o;
        return (this.match.equals(that.match) &&
                this.deleteThings.equals(that.deleteThings) &&
                this.insertThings.equals(that.insertThings));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
