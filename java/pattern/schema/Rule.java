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

package graql.lang.pattern.schema;

import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Definable;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.ThingVariable;

import java.util.List;
import java.util.Objects;

import static graql.lang.common.GraqlToken.Char.COLON;
import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Constraint.THEN;
import static graql.lang.common.GraqlToken.Constraint.WHEN;
import static graql.lang.common.GraqlToken.Type.RULE;
import static java.util.stream.Collectors.toList;

public class Rule implements Definable {
    private final String label;
    private Conjunction<? extends Pattern> when;
    private ThingVariable<?> then;
    private int hash = 0;

    public Rule(String label) {
        this.label = label;
    }

    @Override
    public boolean isRule() {
        return true;
    }

    public Rule when(List<? extends Pattern> patterns) {
        if (patterns == null) throw new NullPointerException("Null when pattern");
        this.when = new Conjunction<>(patterns.stream().map(Objects::requireNonNull).collect(toList()));
        return this;
    }

    public Rule then(ThingVariable<?> pattern) {
        if (pattern == null) throw new NullPointerException("Null then pattern");
        this.then = pattern;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder syntax = new StringBuilder();
        syntax.append(RULE).append(SPACE).append(label).append(COLON);

        // when
        syntax.append(THEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
        if (when != null) syntax.append(then).append(SEMICOLON);
        syntax.append(CURLY_CLOSE);

        // then
        syntax.append(WHEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
        if (then != null) syntax.append(then).append(SEMICOLON).append(SPACE);
        syntax.append(CURLY_CLOSE);

        return syntax.toString();
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            this.hash = Objects.hash(label, when, then);
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Rule) {
            Rule that = (Rule) o;
            return this.label.equals(that.label) && this.when.equals(that.when) && this.then.equals(that.then);
        }
        return false;
    }
}
