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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static graql.lang.Graql.Token.Char.CURLY_CLOSE;
import static graql.lang.Graql.Token.Char.CURLY_OPEN;
import static graql.lang.Graql.Token.Char.SPACE;
import static graql.lang.Graql.Token.Operator.OR;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Disjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;

    public Disjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
        this.patterns = patterns.stream().map(Objects::requireNonNull).collect(toList());
        this.hash = Objects.hash(this.patterns);
    }

    public List<T> patterns() {
        return patterns;
    }

    @Override
    public String toString() {
        StringBuilder disjunction = new StringBuilder();

        Iterator<T> patternIter = patterns.iterator();
        while (patternIter.hasNext()) {
            Pattern pattern = patternIter.next();
            disjunction.append(CURLY_OPEN).append(SPACE);

            if (pattern instanceof Conjunction<?>) {
                Conjunction<?> conjunction = (Conjunction<? extends Pattern>) pattern;
                disjunction.append(conjunction.patterns().stream().map(Object::toString).collect(joining(SPACE.toString())));
            } else {
                disjunction.append(pattern);
            }

            disjunction.append(SPACE).append(CURLY_CLOSE);
            if (patternIter.hasNext()) disjunction.append(SPACE).append(OR).append(SPACE);
        }
        disjunction.append(Graql.Token.Char.SEMICOLON);
        return disjunction.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disjunction<?> that = (Disjunction<?>) o;
        return Objects.equals(patterns, that.patterns);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
