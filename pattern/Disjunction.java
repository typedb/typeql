/*
 * Copyright (C) 2022 Vaticle
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

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Operator.OR;
import static com.vaticle.typeql.lang.common.util.Strings.indent;
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
    public void validateIsBoundedBy(Set<UnboundVariable> bounds) {
        patterns.forEach(pattern -> pattern.validateIsBoundedBy(bounds));
    }

    @Override
    public Disjunction<Conjunction<Conjunctable>> normalise() {
        if (normalised == null) {
            List<Conjunction<Conjunctable>> conjunctions = patterns.stream().flatMap(p -> {
                if (p.isVariable()) return Stream.of(new Conjunction<>(list(p.asConjunctable())));
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
    public boolean isDisjunction() { return true; }

    @Override
    public Disjunction<?> asDisjunction() { return this; }

    @Override
    public String toString() {
        StringBuilder disjunction = new StringBuilder();
        Iterator<T> patternIter = patterns.iterator();
        while (patternIter.hasNext()) {
            Pattern pattern = patternIter.next();
            if (pattern.isConjunction()) disjunction.append(pattern.asConjunction().toString(true));
            else {
                disjunction.append(CURLY_OPEN).append(NEW_LINE);
                disjunction.append(indent(pattern.toString() + SEMICOLON));
                disjunction.append(NEW_LINE).append(CURLY_CLOSE);
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
