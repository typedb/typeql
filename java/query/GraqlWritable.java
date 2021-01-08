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
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.ThingVariable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Command.DELETE;
import static graql.lang.common.GraqlToken.Command.INSERT;
import static graql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static java.util.stream.Collectors.joining;

abstract class GraqlWritable extends GraqlQuery {

    private final GraqlToken.Command keyword;
    private final GraqlMatch.Unfiltered match;
    private final List<ThingVariable<?>> variables;
    private final int hash;

    GraqlWritable(GraqlToken.Command keyword, @Nullable GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        assert keyword == INSERT || keyword == DELETE;
        if (variables == null || variables.isEmpty()) throw GraqlException.of(MISSING_PATTERNS.message());
        this.keyword = keyword;
        this.match = match;
        this.variables = list(variables);
        this.hash = Objects.hash(this.match, this.variables);
    }

    GraqlMatch.Unfiltered nullableMatch() {
        return match;
    }

    public List<ThingVariable<?>> variables() {
        return variables;
    }

    @Override
    public final String toString() {
        final StringBuilder query = new StringBuilder();

        if (match != null) query.append(match).append(NEW_LINE);
        query.append(keyword);

        if (variables.size() > 1) query.append(NEW_LINE);
        else query.append(GraqlToken.Char.SPACE);

        query.append(variables().stream().map(ThingVariable::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GraqlWritable that = (GraqlWritable) o;
        return (this.keyword.equals(that.keyword) &&
                Objects.equals(this.match, that.match) &&
                this.variables.equals(that.variables));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
