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

package graql.lang.query;

import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.common.GraqlToken;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnscopedVariable;
import graql.lang.pattern.variable.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class MatchClause {

    private final Conjunction<? extends Pattern> pattern;
    private final int hash;
    private List<Variable> vars;
    private List<Variable> varsNamedNoProps;

    public MatchClause(Conjunction<? extends Pattern> pattern) {
        if (pattern.patterns().size() == 0) throw GraqlException.create(ErrorMessage.MISSING_PATTERNS.message());
        this.pattern = pattern;
        this.hash = Objects.hash(this.pattern);
    }

    public final Conjunction<? extends Pattern> getPatterns() {
        return pattern;
    }

    public final List<Variable> variables() {
        if (vars == null) vars = pattern.variables().collect(toList());
        return vars;
    }

    final List<Variable> variablesNamedNoProps() {
        if (varsNamedNoProps == null) {
            varsNamedNoProps = pattern.variables().filter(Variable::isNamed)
                    .map(Variable::withoutProperties)
                    .distinct().collect(toList());
        }
        return varsNamedNoProps;
    }

    /**
     * Construct a get query with all all variables mentioned in the query
     */
    public GraqlGet.Unfiltered get() {
        return new GraqlGet.Unfiltered(this);
    }

    /**
     * @param vars an array of variables to select
     * @return a Get Query that selects the given variables
     */
    public GraqlGet.Unfiltered get(String var, String... vars) {
        return get(concat(of(var), of(vars)).map(UnscopedVariable::named).collect(toList()));
    }

    /**
     * @param vars an array of variables to select
     * @return a Get Query that selects the given variables
     */
    public GraqlGet.Unfiltered get(UnscopedVariable var, UnscopedVariable... vars) {
        List<UnscopedVariable> varList = new ArrayList<>();
        varList.add(var);
        varList.addAll(Arrays.asList(vars));
        return get(varList);
    }

    /**
     * @param vars a set of variables to select
     * @return a Get Query that selects the given variables
     */
    public GraqlGet.Unfiltered get(List<UnscopedVariable> vars) {
        return new GraqlGet.Unfiltered(this, vars);
    }

    /**
     * @param things an array of variables to insert for each result of this match clause
     * @return an insert query that will insert the given variables for each result of this match clause
     */
    public final GraqlInsert insert(ThingVariable... things) {
        return new GraqlInsert(this, list(things));
    }

    public final GraqlInsert insert(List<ThingVariable<?>> things) {
        return new GraqlInsert(this, things);
    }

    /**
     * @param things, an array of things that indicate properties to delete
     * @return a delete query that will delete the given variables for each result of this match clause
     */
    public final GraqlDelete delete(ThingVariable<?>... things) {
        return new GraqlDelete(this, list(things));
    }

    public final GraqlDelete delete(List<ThingVariable<?>> things) {
        return new GraqlDelete(this, things);
    }

    @Override
    public final String toString() {
        StringBuilder query = new StringBuilder();
        query.append(GraqlToken.Command.MATCH);

        if (pattern.patterns().size() > 1) query.append(NEW_LINE);
        else query.append(GraqlToken.Char.SPACE);

        query.append(pattern.patterns().stream().map(Object::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchClause other = (MatchClause) o;
        return pattern.equals(other.pattern);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
