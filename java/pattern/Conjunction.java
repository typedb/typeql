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

import graql.lang.Graql;
import graql.lang.variable.Variable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Conjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;

    public Conjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
        this.patterns = patterns.stream().map(Objects::requireNonNull).collect(toList());
        this.hash = Objects.hash(this.patterns);
    }

    public Stream<Variable> variables() {
        return patterns.stream().flatMap(pattern -> {
            if (pattern instanceof Variable) return ((Variable) pattern).variables();
            else if (pattern instanceof Conjunction) return ((Conjunction<?>) pattern).variables();
            else return Stream.of();
        });
    }

    public List<T> patterns() {
        return patterns;
    }

    @Override
    public String toString() {
        StringBuilder pattern = new StringBuilder();
        pattern.append(Graql.Token.Char.CURLY_OPEN).append(Graql.Token.Char.SPACE);
        pattern.append(patterns.stream().map(Objects::toString).collect(Collectors.joining(Graql.Token.Char.SPACE.toString())));
        pattern.append(Graql.Token.Char.SPACE).append(Graql.Token.Char.CURLY_CLOSE).append(Graql.Token.Char.SEMICOLON);
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
