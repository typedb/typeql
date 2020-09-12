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

package graql.lang.pattern.constraint;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.SchemaVariable;

import java.util.Objects;
import java.util.Set;

import static grakn.common.collection.Collections.set;
import static grakn.common.util.Objects.className;
import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Constraint.THEN;
import static graql.lang.common.GraqlToken.Constraint.WHEN;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static java.util.stream.Collectors.joining;

public abstract class SchemaConstraint extends Constraint<SchemaVariable> {

    @Override
    public Set<SchemaVariable> variables() {
        return set();
    }


    public boolean isThen() {
        return false;
    }

    public boolean isWhen() {
        return false;
    }

    public SchemaConstraint.Then asThen() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Then.class)));
    }

    public SchemaConstraint.When asWhen() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(When.class)));
    }

    public static class Then extends SchemaConstraint {

        private final Pattern pattern;
        private final int hash;

        public Then(Pattern pattern) {
            if (pattern == null) throw new NullPointerException("Null pattern");
            this.pattern = pattern;
            this.hash = Objects.hash(Then.class, this.pattern);
        }

        public Pattern pattern() {
            return pattern;
        }

        @Override
        public boolean isThen() {
            return true;
        }

        @Override
        public Then asThen() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            syntax.append(THEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
            if (pattern instanceof Conjunction) {
                syntax.append(((Conjunction<?>) pattern).patterns()
                        .stream().map(Object::toString)
                        .collect(joining("" + SEMICOLON + SPACE)));
            } else {
                syntax.append(pattern.toString());
            }
            syntax.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
            return syntax.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Then that = (Then) o;
            return (this.pattern.equals(that.pattern));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class When extends SchemaConstraint {

        private final Pattern pattern;
        private final int hash;

        public When(Pattern pattern) {
            if (pattern == null) throw new NullPointerException("Null Pattern");
            this.pattern = pattern;
            this.hash = Objects.hash(When.class, this.pattern);
        }

        public Pattern pattern() {
            return pattern;
        }

        @Override
        public boolean isWhen() {
            return true;
        }

        @Override
        public When asWhen() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            syntax.append(WHEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
            if (pattern instanceof Conjunction) {
                syntax.append(((Conjunction<?>) pattern).patterns()
                        .stream().map(Object::toString)
                        .collect(joining("" + SEMICOLON + SPACE)));
            } else {
                syntax.append(pattern);
            }
            syntax.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
            return syntax.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            When that = (When) o;
            return (this.pattern.equals(that.pattern));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
