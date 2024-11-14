/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern;

import com.typeql.lang.common.TypeQLVariable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.typedb.common.collection.Collections.list;
import static com.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.typeql.lang.common.TypeQLToken.Operator.OR;
import static com.typeql.lang.common.util.Strings.indent;
import static java.util.stream.Collectors.toList;

public class Disjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;
    private Disjunction<Conjunction<Conjunctable>> normalised;

    public Disjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
        this.patterns = patterns.stream().map(Objects::requireNonNull).collect(toList());
        this.hash = Objects.hash(this.patterns);
    }

    @Override
    public List<T> patterns() {
        return patterns;
    }

    @Override
    public void validateIsBoundedBy(Set<TypeQLVariable> bounds) {
        patterns.forEach(pattern -> pattern.validateIsBoundedBy(bounds));
    }

    @Override
    public Disjunction<Conjunction<Conjunctable>> normalise() {
        if (normalised == null) {
            List<Conjunction<Conjunctable>> conjunctions = patterns.stream().flatMap(p -> {
                if (p.isStatement()) return Stream.of(new Conjunction<>(list(p.asConjunctable())));
                else if (p.isNegation())
                    return Stream.of(new Conjunction<>(list(p.asNegation().normalise().asConjunctable())));
                else if (p.isConjunction()) return p.asConjunction().normalise().patterns().stream();
                else return p.asDisjunction().normalise().patterns().stream();
            }).collect(toList());
            normalised = new Disjunction<>(conjunctions);
        }
        return normalised;
    }

    @Override
    public boolean isDisjunction() {
        return true;
    }

    @Override
    public Disjunction<?> asDisjunction() {
        return this;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder disjunction = new StringBuilder();
        Iterator<T> patternIter = patterns.iterator();
        while (patternIter.hasNext()) {
            Pattern pattern = patternIter.next();
            if (pattern.isConjunction()) disjunction.append(pattern.asConjunction().toString(pretty));
            else {
                disjunction.append(CURLY_OPEN);
                if (pretty) {
                    disjunction.append(NEW_LINE).append(indent(pattern.toString(pretty) + SEMICOLON))
                            .append(NEW_LINE);
                } else {
                    disjunction.append(pattern.toString(pretty)).append(SEMICOLON);
                }
                disjunction.append(CURLY_CLOSE);
            }
            if (patternIter.hasNext()) disjunction.append(SPACE).append(OR).append(SPACE);
        }
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
