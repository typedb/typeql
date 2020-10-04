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

package graql.lang.pattern;

import graql.lang.pattern.variable.BoundVariable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class Conjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;

    public Conjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
        this.patterns = patterns.stream().map(Objects::requireNonNull).collect(toList());
        this.hash = Objects.hash(this.patterns);
    }

    public Stream<BoundVariable> variables() {
        return patterns.stream().flatMap(pattern -> {
            if (pattern instanceof BoundVariable)
                return concat(of(((BoundVariable) pattern)), ((BoundVariable) pattern).variables());
            else if (pattern instanceof Conjunction) return ((Conjunction<?>) pattern).variables();
            else return of();
        });
    }

    public List<T> patterns() {
        return patterns;
    }

    @Override
    public boolean isConjunction() { return true; }

    @Override
    public Conjunction<?> asConjunction() { return this; }

    @Override
    public String toString() {
        StringBuilder pattern = new StringBuilder();
        pattern.append(CURLY_OPEN).append(SPACE);
        pattern.append(patterns.stream().map(Objects::toString).collect(joining("" + SEMICOLON + SPACE)));
        pattern.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
        return pattern.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conjunction<?> that = (Conjunction<?>) o;
        return Objects.equals(patterns, that.patterns);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
