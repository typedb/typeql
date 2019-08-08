/*
 * GRAKN.AI - THE KNOWLEDGE GRAPH
 * Copyright (C) 2019 Grakn Labs Ltd
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

import com.google.common.collect.Sets;
import graql.lang.Graql;
import graql.lang.statement.Statement;
import graql.lang.statement.Variable;

import javax.annotation.CheckReturnValue;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * A class representing a conjunction (and) of patterns. All inner patterns must match in a query
 *
 * @param <T> the type of patterns in this conjunction
 */
public class Conjunction<T extends Pattern> implements Pattern {

    private final LinkedHashSet<T> patterns;

    public Conjunction(Set<T> patterns) {
        if (patterns == null) {
            throw new NullPointerException("Null patterns");
        }
        this.patterns = patterns.stream()
                .map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
        return patterns.hashCode();
    }

    /**
     * @return the patterns within this conjunction
     */
    @CheckReturnValue
    public Set<T> getPatterns() {
        return patterns;
    }

    @Override
    public Disjunction<Conjunction<Statement>> getDisjunctiveNormalForm() {
        // Get all disjunctions in query
        List<Set<Conjunction<Statement>>> disjunctionsOfConjunctions = getPatterns().stream()
                .map(p -> p.getDisjunctiveNormalForm().getPatterns())
                .collect(toList());

        // Get the cartesian product.
        // in other words, this puts the 'ands' on the inside and the 'ors' on the outside
        // e.g. (A or B) and (C or D)  <=>  (A and C) or (A and D) or (B and C) or (B and D)
        Set<Conjunction<Statement>> dnf = Sets.cartesianProduct(disjunctionsOfConjunctions).stream()
                .map(Conjunction::fromConjunctions)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return Graql.or(dnf);

        // Wasn't that a horrible function? Here it is in Haskell:
        //     dnf = map fromConjunctions . sequence . map getDisjunctiveNormalForm . patterns
    }

    @Override
    public Disjunction<Conjunction<Pattern>> getNegationDNF() {
        List<Set<Conjunction<Pattern>>> disjunctionsOfConjunctions = getPatterns().stream()
                .map(p -> p.getNegationDNF().getPatterns())
                .collect(toList());

        Set<Conjunction<Pattern>> dnf = Sets.cartesianProduct(disjunctionsOfConjunctions).stream()
                .map(Conjunction::fromConjunctions)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Graql.or(dnf);
    }

    @Override
    public Set<Variable> variables() {
        return getPatterns().stream().map(Pattern::variables).reduce(new HashSet<>(), Sets::union);
    }

    private static <U extends Pattern> Conjunction<U> fromConjunctions(List<Conjunction<U>> conjunctions) {
        Set<U> patterns = conjunctions.stream()
                .flatMap(p -> p.getPatterns().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Graql.and(patterns);
    }

    @Override
    public String toString() {
        StringBuilder pattern = new StringBuilder();

        pattern.append(Graql.Token.Char.CURLY_OPEN).append(Graql.Token.Char.SPACE);
        pattern.append(patterns.stream().map(Objects::toString).collect(Collectors.joining(Graql.Token.Char.SPACE.toString())));
        pattern.append(Graql.Token.Char.SPACE).append(Graql.Token.Char.CURLY_CLOSE).append(Graql.Token.Char.SEMICOLON);

        return pattern.toString();
    }
}
