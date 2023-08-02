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

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_UNBOUNDED_NESTED_PATTERN;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.vaticle.typeql.lang.common.util.Strings.indent;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class Conjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;
    private Disjunction<Conjunction<Conjunctable>> normalised;

    public Conjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
        else if (patterns.size() == 0) throw TypeQLException.of(MISSING_PATTERNS);
        this.patterns = patterns.stream().map(Objects::requireNonNull).collect(toList());
        this.hash = Objects.hash(this.patterns);
    }

    public Stream<BoundVariable> variables() {
        return patterns.stream().flatMap(pattern -> {
            if (pattern.isVariable()) return concat(Stream.of(pattern.asVariable()), pattern.asVariable().variables());
            else if (pattern.isConjunction()) return pattern.asConjunction().variables();
            else return Stream.of();
        });
    }

    public Stream<UnboundVariable> namedVariablesUnbound() {
        return variables().filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct();
    }

    @Override
    public List<T> patterns() {
        return patterns;
    }

    @Override
    public void validateIsBoundedBy(Set<UnboundVariable> bounds) {
        if (variables().noneMatch(v -> bounds.contains(v.toUnbound()))) {
            String str = toString().replace("\n", " ");
            throw TypeQLException.of(MATCH_HAS_UNBOUNDED_NESTED_PATTERN.message(str));
        }
        HashSet<UnboundVariable> union = new HashSet<>(bounds);
        union.addAll(variables().map(BoundVariable::toUnbound).collect(Collectors.toSet()));
        patterns.stream().filter(pattern -> !pattern.isVariable()).forEach(pattern -> {
            pattern.validateIsBoundedBy(union);
        });
    }

    public static <U extends Pattern> Conjunction<U> merge(List<Conjunction<U>> conjunctions) {
        return new Conjunction<>(conjunctions.stream().flatMap(p -> p.patterns().stream()).collect(toList()));
    }

    @Override
    public Disjunction<Conjunction<Conjunctable>> normalise() {
        if (normalised == null) {
            List<Conjunctable> conjunctables = new ArrayList<>();
            List<List<Conjunction<Conjunctable>>> listOfDisj = new ArrayList<>();
            patterns.forEach(pattern -> {
                if (pattern.isVariable()) conjunctables.add(pattern.asVariable().normalise());
                else if (pattern.isNegation()) conjunctables.add(pattern.asNegation().normalise());
                else if (pattern.isConjunction()) listOfDisj.add(pattern.asConjunction().normalise().patterns());
                else listOfDisj.add(pattern.asDisjunction().normalise().patterns());
            });
            listOfDisj.add(list(new Conjunction<>(conjunctables)));
            List<Conjunction<Conjunctable>> listOfConjunctions = new CartesianList<>(listOfDisj)
                    .stream().map(Conjunction::merge)
                    .collect(toList());
            normalised = new Disjunction<>(listOfConjunctions);
        }
        return normalised;
    }

    @Override
    public boolean isConjunction() { return true; }

    @Override
    public Conjunction<?> asConjunction() { return this; }

    @Override
    public String toString() {
        return toString(patterns.size() > 1 || patterns.get(0).toString().lines().count() > 1);
    }

    @Override
    public String toString(boolean pretty) {
        if (pretty) {
            TypeQLToken.Char whitespace = NEW_LINE;
            String body = patterns.stream().map(p -> p.toString(pretty)).collect(SEMICOLON_NEW_LINE.joiner()) + SEMICOLON;
            body = indent(body);
            return CURLY_OPEN.toString() + whitespace + body + whitespace + CURLY_CLOSE;
        } else {
            TypeQLToken.Char whitespace = SPACE;
            String body = patterns.stream().map(p -> p.toString(pretty)).collect(SEMICOLON_SPACE.joiner()) + SEMICOLON;
            return CURLY_OPEN.toString() + whitespace + body + whitespace + CURLY_CLOSE;
        }
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

    private static class CartesianList<E> extends AbstractList<List<E>> {

        private final transient List<List<E>> axes;
        private final transient int[] axesSizeProduct;
        private final Map<Integer, List<E>> computed;

        CartesianList(List<List<E>> axes) {
            this.axes = axes;
            axesSizeProduct = new int[axes.size() + 1];
            axesSizeProduct[axes.size()] = 1;
            for (int i = axes.size() - 1; i >= 0; i--) {
                axesSizeProduct[i] = axesSizeProduct[i + 1] * axes.get(i).size();
            }
            if (axesSizeProduct[0] == 0) throw TypeQLException.of(ErrorMessage.ILLEGAL_STATE);
            computed = new HashMap<>();
        }

        private int getAxisIndexForProductIndex(int index, int axis) {
            return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
        }

        @Override
        public List<E> get(int index) {
            if (index >= size()) throw new IndexOutOfBoundsException();

            return computed.computeIfAbsent(index, i -> new AbstractList<E>() {

                @Override
                public int size() {
                    return axes.size();
                }

                @Override
                public E get(int axis) {
                    if (axis >= size()) throw new IndexOutOfBoundsException();
                    return axes.get(axis).get(getAxisIndexForProductIndex(i, axis));
                }
            });
        }

        @Override
        public int size() {
            return axesSizeProduct[0];
        }
    }
}
