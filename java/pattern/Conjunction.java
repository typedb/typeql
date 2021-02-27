/*
 * Copyright (C) 2021 Grakn Labs
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

import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.pattern.variable.Variable;

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

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.MATCH_HAS_UNBOUNDED_NESTED_PATTERN;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class Conjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;
    private Disjunction<Conjunction<Conjunctable>> normalised;

    public Conjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
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
        return variables().filter(Variable::isNamed).map(v -> UnboundVariable.named(v.name())).distinct();
    }

    @Override
    public List<T> patterns() {
        return patterns;
    }

    @Override
    public void validateIsBoundedBy(Set<UnboundVariable> bounds) {
        if (variables().noneMatch(v -> bounds.contains(v.toUnbound()))) {
            String str = toString().replace("\n", " ");
            throw GraqlException.of(MATCH_HAS_UNBOUNDED_NESTED_PATTERN.message(str));
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
        StringBuilder pattern = new StringBuilder();
        pattern.append(CURLY_OPEN).append(SPACE);
        pattern.append(patterns.stream().map(Objects::toString).collect(joining("" + SEMICOLON + SPACE)));
        pattern.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
        return pattern.toString();
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
            if (axesSizeProduct[0] == 0) throw GraqlException.of(ErrorMessage.ILLEGAL_STATE);
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
