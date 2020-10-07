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

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Definable;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.Reference;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.Variable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static graql.lang.common.GraqlToken.Char.COLON;
import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Schema.RULE;
import static graql.lang.common.GraqlToken.Schema.THEN;
import static graql.lang.common.GraqlToken.Schema.WHEN;
import static graql.lang.common.exception.ErrorMessage.INVALID_RULE_THEN_MUST_BE_ONE_CONSTRAINT;
import static graql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;

public class Rule implements Definable {
    private final String label;
    private Conjunction<? extends Pattern> when;
    private ThingVariable<?> then;
    private int hash = 0;

    public Rule(final String label) {
        this.label = label;
    }

    @Override
    public boolean isRule() {
        return true;
    }

    @Override
    public Rule asRule() {
        return this;
    }

    public String label() { return label; }

    public Conjunction<? extends Pattern> when() { return when; }

    public ThingVariable<?> then() { return then; }

    public Rule when(final Conjunction<? extends Pattern> when) {
        if (when == null) throw new NullPointerException("Null when pattern");
        if (when.patterns().size() == 0) throw GraqlException.of(MISSING_PATTERNS.message());
        this.when = when;
        return this;
    }

    public Rule then(final ThingVariable<?> variable) {
        if (variable == null) throw new NullPointerException("Null then pattern");

        int numConstraints = variable.constraints().size();


        if (numConstraints == 0     ||
            numConstraints > 2      ||
            numConstraints == 1 && !(variable.relation().isPresent() || variable.has().size() == 1 || variable.isa().isPresent())) {
            throw new RuntimeException(String.format("In rule '%s', 'then' may only conclude one new or extended relation, one attribute ownership, or one 'isa' downcast: " +variable, label()));
        }

        if (variable.constraints().size() == 2) {
            if (!variable.relation().isPresent() && !variable.isa().isPresent()) {
                // rule concludes a relation or additional role players
                throw new RuntimeException("Rule 'then' with two constraints must consist of a relation and isa constraint." + variable); // TODO improve
            }
        }

        if (when() != null){
            // TODO and all user-written variables must be present in the when clause, if it exists
            Set<Reference> thenReferences = new HashSet<>();
            if (variable.isNamed()) thenReferences.add(variable.reference());
            variable.constraints().stream().flatMap(c -> c.variables().stream()).filter(Variable::isNamed).map(Variable::reference).forEach(thenReferences::add);

            Set<Reference> whenReferences = new HashSet<>();
            when().patterns().forEach(p -> {
                BoundVariable var = p.asVariable();
                if (var.isNamed()) whenReferences.add(var.reference());
                var.constraints().forEach(c -> {
                    c.variables().stream().filter(Variable::isNamed).map(Variable::reference).forEach(whenReferences::add);
                });
            });

            if (!whenReferences.containsAll(thenReferences)) {
                throw new RuntimeException("All variables in rule 'then' must be present in rule 'when'");
            }
        }


        this.then = variable;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder syntax = new StringBuilder();
        syntax.append(RULE).append(SPACE).append(label);

        if (when != null || then != null) syntax.append(COLON).append(SPACE);

        // when
        if (when != null) {
            syntax.append(WHEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
            for (Pattern p : when.patterns()) {
                syntax.append(p).append(SEMICOLON).append(SPACE);
            }
            syntax.append(CURLY_CLOSE).append(SPACE);
        }

        // then
        if (then != null) {
            syntax.append(THEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
            syntax.append(then).append(SEMICOLON).append(SPACE);
            syntax.append(CURLY_CLOSE);
        }

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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Rule that = (Rule) o;
        return (this.label.equals(that.label) &&
                Objects.equals(this.when, that.when) &&
                Objects.equals(this.then, that.then));
    }
}
