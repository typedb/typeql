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

import graql.lang.common.GraqlToken;

import java.util.Objects;

import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;

/**
 * A class representing a negation of patterns. All inner patterns must not match in a query.
 *
 * @param <T> the type of patterns in this negation
 */
public class Negation<T extends Pattern> implements Pattern {

    private final T pattern;

    public Negation(T pattern) {
        if (pattern == null) throw new NullPointerException("Null patterns");
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Negation<?> negation = (Negation<?>) o;
        return Objects.equals(pattern, negation.pattern);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }

    public T getPattern() { return pattern;}

    @Override
    public boolean isNegation() { return true; }

    @Override
    public Negation<?> asNegation() { return this; }

    @Override
    public String toString() {
        StringBuilder negation = new StringBuilder();
        negation.append(GraqlToken.Operator.NOT).append(SPACE);

        if (pattern instanceof Conjunction<?>) {
            negation.append(pattern.toString());
        } else {
            negation.append(CURLY_OPEN).append(SPACE);
            negation.append(pattern.toString()).append(SEMICOLON);
            negation.append(SPACE).append(CURLY_CLOSE);
        }

        return negation.toString();
    }
}

