/*
 * Copyright (C) 2021 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typeql.lang.pattern.schema;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.Disjunction;
import com.vaticle.typeql.lang.pattern.Negation;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.Reference;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Schema.RULE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Schema.THEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Schema.WHEN;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_RULE_THEN;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_RULE_THEN_HAS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_RULE_THEN_VARIABLES;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_RULE_WHEN_CONTAINS_DISJUNCTION;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_RULE_WHEN_MISSING_PATTERNS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_RULE_WHEN_NESTED_NEGATION;
import static com.vaticle.typeql.lang.common.util.Strings.indent;

public class Rule implements Definable {
    private final String label;
    private Conjunction<? extends Pattern> when;
    private ThingVariable<?> then;
    private int hash = 0;

    public Rule(String label) {
        this.label = label;
    }

    public Rule(String label, Conjunction<? extends Pattern> when, ThingVariable<?> variable) {
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
        if (when.patterns().size() == 0) throw TypeQLException.of(INVALID_RULE_WHEN_MISSING_PATTERNS.message(label));
        if (findNegations(when).anyMatch(negation -> findNegations(negation.pattern()).findAny().isPresent())) {
            throw TypeQLException.of(INVALID_RULE_WHEN_NESTED_NEGATION.message(label));
        }
        if (findDisjunctions(when).findAny().isPresent()) {
            throw TypeQLException.of(INVALID_RULE_WHEN_CONTAINS_DISJUNCTION.message(label));
        }
    }

    private static Stream<Negation<?>> findNegations(Pattern pattern) {
        if (pattern.isNegation()) return Stream.of(pattern.asNegation());
        if (pattern.isVariable()) return Stream.empty();
        return pattern.patterns().stream().flatMap(Rule::findNegations);
    }

    private static Stream<Disjunction<?>> findDisjunctions(Pattern pattern) {
        if (pattern.isDisjunction()) return Stream.of(pattern.asDisjunction());
        if (pattern.isVariable()) return Stream.empty();
        return pattern.patterns().stream().flatMap(Rule::findDisjunctions);
    }

    private static void validateThen(String label, @Nullable Conjunction<? extends Pattern> when, ThingVariable<?> then) {
        if (then == null) throw new NullPointerException("Null then pattern");
        int numConstraints = then.constraints().size();

        // rules must contain contain either 1 has constraint, or a isa and relation constrain
        if (!((numConstraints == 1 && then.has().size() == 1) || (numConstraints == 2 && then.relation().isPresent() && then.isa().isPresent()))) {
            throw TypeQLException.of(INVALID_RULE_THEN.message(label, then));
        }

        // rule 'has' cannot assign both an attribute type and a named variable
        if (then.has().size() == 1 && then.has().get(0).type().isPresent() && then.has().get(0).attribute().isNamed()) {
            String attrVar = then.has().get(0).attribute().name();
            String attrType;
            if (then.has().get(0).type().get().label().isPresent()) {
                attrType = then.has().get(0).type().get().label().get().label();
            } else {
                assert then.has().get(0).type().get().isNamed();
                attrType = then.has().get(0).type().get().name();
            }
            throw TypeQLException.of(INVALID_RULE_THEN_HAS.message(label, then, attrType, attrVar));
        }

        // all user-written variables in the 'then' must be present in the 'when', if it exists.
        if (when != null) {
            Set<Reference> thenReferences = Stream.concat(Stream.of(then), then.variables())
                    .filter(Variable::isNamed).map(Variable::reference).collect(Collectors.toSet());

            Set<Reference> whenReferences = when.variables()
                    .filter(Variable::isNamed).map(Variable::reference).collect(Collectors.toSet());

            if (!whenReferences.containsAll(thenReferences)) {
                throw TypeQLException.of(INVALID_RULE_THEN_VARIABLES.message(label));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder rule = new StringBuilder("" + RULE + SPACE + label);
        if (when != null) {
            rule.append(COLON).append(NEW_LINE);
            StringBuilder body = new StringBuilder();
            body.append(WHEN).append(SPACE).append(when.toString(true)).append(NEW_LINE);
            body.append(THEN).append(SPACE).append(CURLY_OPEN).append(NEW_LINE);
            body.append(indent(then)).append(SEMICOLON);
            body.append(NEW_LINE).append(CURLY_CLOSE);
            rule.append(indent(body));
        }
        return rule.toString();
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule that = (Rule) o;
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
