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

import com.sun.org.apache.xpath.internal.operations.Neg;
import com.sun.tools.corba.se.idl.constExpr.Negative;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.*;
import graql.lang.pattern.variable.Reference;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.Variable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static graql.lang.common.GraqlToken.Char.COLON;
import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Schema.RULE;
import static graql.lang.common.GraqlToken.Schema.THEN;
import static graql.lang.common.GraqlToken.Schema.WHEN;
import static graql.lang.common.exception.ErrorMessage.*;

public class Rule implements Definable {
    private final String label;
    private Conjunction<? extends Pattern> when;
    private ThingVariable<?> then;
    private int hash = 0;

    public Rule(final String label) {
        this.label = label;
    }

    public Rule(final String label, final Conjunction<? extends Pattern> when, final ThingVariable<?> variable) {
        validate(label, when, variable);
        this.label = label;
        this.when = when;
        this.then = variable;
    }

    @Override
    public boolean isRule() {
        return true;
    }

    @Override
    public Rule asRule() {
        return this;
    }

    public String label() {
        return label;
    }

    public Conjunction<? extends Pattern> when() {
        return when;
    }

    public ThingVariable<?> then() {
        return then;
    }

    public IncompleteRule when(Conjunction<? extends Pattern> when) {
        return new IncompleteRule(label, when);
    }

    public static void validate(String label, Conjunction<? extends Pattern> when, ThingVariable<?> then) {
        validateWhen(label, when);
        validateThen(label, when, then);
    }

    private static void validateWhen(String label, Conjunction<? extends Pattern> when) {
        if (when == null) throw new NullPointerException("Null when pattern");
        if (when.patterns().size() == 0) throw GraqlException.of(INVALID_RULE_WHEN_MISSING_PATTERNS.message(label));
        if (findNegations(when).stream().anyMatch(negation -> findNegations(negation.pattern()).isEmpty())) {
            throw GraqlException.of(INVALID_RULE_WHEN_NESTED_NEGATION.message(label));
        }

    }

    private static Set<Negation> findNegations(Pattern pattern) {
        if (pattern.isNegation()){
            return Collections.singleton(pattern.asNegation());
        } else if (pattern.isVariable()) {
            return Collections.emptySet();
        }
        Set<? extends Pattern> innerPatterns = pattern.patterns();
        return innerPatterns.stream().flatMap(patt -> findNegations(patt).stream()).collect(Collectors.toSet());
    }



    //TODO check if the change in INVALID_RULE_THEN_ONE_CONSTRAINT validation requires reworking of logic here or elsewhere
    private static void validateThen(String label, @Nullable Conjunction<? extends Pattern> when, ThingVariable<?> then) {
        if (then == null) throw new NullPointerException("Null then pattern");
        int numConstraints = then.constraints().size();

        // rules may only conclude one 'has', 'relation', or 'isa' constraint
        if (numConstraints == 0 || numConstraints > 2 || numConstraints == 1 &&
                !(then.relation().isPresent() || then.has().size() == 1 || then.isa().isPresent())) {
            throw GraqlException.of(INVALID_RULE_THEN_ONE_CONSTRAINT.message(label, then));
        }

        // rules with 'relation' conclusions may also have a explicit 'isa' constraint
        if (then.constraints().size() == 2 && !then.relation().isPresent() && !then.isa().isPresent()) {
            throw GraqlException.of(INVALID_RULE_THEN_TWO_CONSTRAINTS.message(label, then));
        }

        // all user-written variables in the 'then' must be present in the 'when', if it exists
        if (when != null) {
            Set<Reference> thenReferences = Stream.concat(Stream.of(then), then.variables())
                    .filter(Variable::isNamed).map(Variable::reference).collect(Collectors.toSet());

            Set<Reference> whenReferences = when.variables()
                    .filter(Variable::isNamed).map(Variable::reference).collect(Collectors.toSet());

            if (!whenReferences.containsAll(thenReferences)) {
                throw GraqlException.of(INVALID_RULE_THEN_VARIABLES.message(label));
            }
        }
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

    public static class IncompleteRule {
        private final String label;
        private final Conjunction<? extends Pattern> when;

        public IncompleteRule(String label, Conjunction<? extends Pattern> when) {
            this.label = label;
            this.when = when;
        }

        public Rule then(ThingVariable<?> then) {
            return new Rule(label, when, then);
        }
    }
}
