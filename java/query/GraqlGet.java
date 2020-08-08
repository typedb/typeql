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

import graql.lang.Graql;
import graql.lang.common.exception.GraqlException;
import graql.lang.query.builder.Aggregatable;
import graql.lang.query.builder.Filterable;
import graql.lang.variable.UnscopedVariable;
import graql.lang.variable.Variable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static grakn.common.util.Collections.list;
import static graql.lang.Graql.Token.Char.COMMA_SPACE;
import static graql.lang.Graql.Token.Char.NEW_LINE;
import static graql.lang.Graql.Token.Char.SEMICOLON;
import static graql.lang.Graql.Token.Char.SPACE;
import static java.util.stream.Collectors.joining;

public class GraqlGet extends GraqlQuery implements Filterable, Aggregatable<GraqlGet.Aggregate> {

    private final List<UnscopedVariable> vars;
    private final MatchClause match;
    private final Sorting sorting;
    private final long offset;
    private final long limit;
    private final int hash;

    GraqlGet(MatchClause match) {
        this(match, list());
    }

    GraqlGet(MatchClause match, List<UnscopedVariable> vars) {
        this(match, vars, null, -1, -1); // TODO replace -1 with NULL
    }

    // We keep this contructor 'public' as it is more efficient for use during parsing
    public GraqlGet(MatchClause match, List<UnscopedVariable> vars, Sorting sorting, long offset, long limit) {
        if (match == null) throw new NullPointerException("Null match");
        if (vars == null) throw new NullPointerException("Null vars");
        for (UnscopedVariable var : vars) {
            if (!match.variablesNamedNoProps().contains(var))
                throw GraqlException.variableOutOfScope(var.toString());
        }
        List<? extends Variable> sortableVars = vars.isEmpty() ? match.variablesNamedNoProps() : vars;
        if (sorting != null && !sortableVars.contains(sorting.var())) {
            throw GraqlException.variableOutOfScope(sorting.var().toString());
        }

        this.match = match;
        this.vars = vars;
        this.sorting = sorting;
        this.offset = offset;
        this.limit = limit;

        // It is important that we use vars() (the method) and not vars (the property)
        // For reasons explained in the equals() method above
        // TODO replace the other methods with fields once we replace offset and limit type from 'int' to 'Integer'
        this.hash = Objects.hash(variables(), match(), sort(), offset(), limit());
    }

    @Override
    public Aggregate aggregate(Graql.Token.Aggregate.Method method, UnscopedVariable var) {
        return new Aggregate(this, method, var);
    }

    public Group group(String var) {
        return group(UnscopedVariable.named(var));
    }

    public Group group(UnscopedVariable var) {
        return new Group(this, var);
    }

    public List<? extends Variable> variables() {
        if (vars.isEmpty()) return match.variablesNamedNoProps();
        else return vars;
    }

    public MatchClause match() {
        return match;
    }

    @Override
    public Optional<Sorting> sort() {
        return Optional.ofNullable(sorting);
    }

    @Override
    public Optional<Long> offset() {
        return offset < 0 ? Optional.empty() : Optional.of(offset);
    }

    @Override
    public Optional<Long> limit() {
        return limit < 0 ? Optional.empty() : Optional.of(limit);
    }

    @Override
    public String toString() {
        StringBuilder query = new StringBuilder(match().toString());
        if (match().getPatterns().patterns().size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);

        query.append(Graql.Token.Command.GET);
        if (!vars.isEmpty()) { // Which is not equal to !vars().isEmpty()
            String varsStr = vars.stream().map(UnscopedVariable::toString).collect(joining(COMMA_SPACE.toString()));
            query.append(SPACE).append(varsStr);
        }
        query.append(SEMICOLON);
        if (sort().isPresent() || offset().isPresent() || limit().isPresent()) {
            query.append(SPACE).append(printFilters());
        }

        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }

        GraqlGet that = (GraqlGet) o;

        // It is important that we use vars() (the method) and not vars (the property)
        // vars (the property) stores the variables as the user defined
        // vars() (the method) returns match.vars() if vars (the property) is empty
        // we want to compare vars() (the method) which determines the final value
        return (this.variables().equals(that.variables()) &&
                this.match().equals(that.match()) &&
                this.sort().equals(that.sort()) &&
                this.offset().equals(that.offset()) &&
                this.limit().equals(that.limit()));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unfiltered extends GraqlGet
            implements Filterable.Unfiltered<GraqlGet.Sorted, GraqlGet.Offsetted, GraqlGet.Limited> {

        Unfiltered(MatchClause match) {
            super(match);
        }

        Unfiltered(MatchClause match, List<UnscopedVariable> vars) {
            super(match, vars);
        }

        @Override
        public GraqlGet.Sorted sort(Sorting sorting) {
            return new GraqlGet.Sorted(this, sorting);
        }

        @Override
        public GraqlGet.Offsetted offset(long offset) {
            return new GraqlGet.Offsetted(this, offset);
        }

        @Override
        public GraqlGet.Limited limit(long limit) {
            return new GraqlGet.Limited(this, limit);
        }
    }

    public static class Sorted extends GraqlGet implements Filterable.Sorted<GraqlGet.Offsetted, GraqlGet.Limited> {

        Sorted(GraqlGet graqlGet, Sorting sorting) {
            super(graqlGet.match, graqlGet.vars, sorting, graqlGet.offset, graqlGet.limit);
        }

        @Override
        public GraqlGet.Offsetted offset(long offset) {
            return new GraqlGet.Offsetted(this, offset);
        }

        @Override
        public GraqlGet.Limited limit(long limit) {
            return new GraqlGet.Limited(this, limit);
        }
    }

    public static class Offsetted extends GraqlGet implements Filterable.Offsetted<GraqlGet.Limited> {

        Offsetted(GraqlGet graqlGet, long offset) {
            super(graqlGet.match, graqlGet.vars, graqlGet.sorting, offset, graqlGet.limit);
        }

        @Override
        public GraqlGet.Limited limit(long limit) {
            return new GraqlGet.Limited(this, limit);
        }
    }

    public static class Limited extends GraqlGet implements Filterable.Limited {

        Limited(GraqlGet graqlGet, long limit) {
            super(graqlGet.match, graqlGet.vars, graqlGet.sorting, graqlGet.offset, limit);
        }
    }

    public static class Aggregate extends GraqlQuery {

        private final GraqlGet query;
        private final Graql.Token.Aggregate.Method method;
        private final UnscopedVariable var;
        private final int hash;

        Aggregate(GraqlGet query, Graql.Token.Aggregate.Method method, UnscopedVariable var) {
            if (query == null) throw new NullPointerException("GetQuery is null");
            if (method == null) throw new NullPointerException("Method is null");


            if (var == null && !method.equals(Graql.Token.Aggregate.Method.COUNT)) {
                throw new NullPointerException("Variable is null");
            } else if (var != null && method.equals(Graql.Token.Aggregate.Method.COUNT)) {
                throw new IllegalArgumentException("Aggregate COUNT does not accept a Variable");
            } else if (var != null && !query.variables().contains(var)) {
                throw GraqlException.variableOutOfScope(var.toString());
            }

            this.query = query;
            this.method = method;
            this.var = var;
            this.hash = Objects.hash(query, method, var);
        }

        public GraqlGet query() {
            return query;
        }

        public Graql.Token.Aggregate.Method method() {
            return method;
        }

        public UnscopedVariable var() {
            return var;
        }

        @Override
        public final String toString() {
            StringBuilder query = new StringBuilder();

            query.append(query()).append(SPACE).append(method);
            if (var != null) query.append(SPACE).append(var);
            query.append(SEMICOLON);

            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Aggregate that = (Aggregate) o;

            return (this.query.equals(that.query) &&
                    this.method.equals(that.method) &&
                    Objects.equals(this.var, that.var));
        }

        @Override
        public int hashCode() {
            return hash;
        }

    }

    public static class Group extends GraqlQuery implements Aggregatable<Group.Aggregate> {

        private final GraqlGet query;
        private final UnscopedVariable var;
        private final int hash;

        Group(GraqlGet query, UnscopedVariable var) {
            if (query == null) throw new NullPointerException("GetQuery is null");
            if (var == null) throw new NullPointerException("Variable is null");
            else if (!query.variables().contains(var)) throw GraqlException.variableOutOfScope(var.toString());

            this.query = query;
            this.var = var;
            this.hash = Objects.hash(query, var);
        }

        public GraqlGet query() {
            return query;
        }

        public UnscopedVariable var() {
            return var;
        }

        @Override
        public Aggregate aggregate(Graql.Token.Aggregate.Method method, UnscopedVariable var) {
            return new Aggregate(this, method, var);
        }

        @Override
        public String toString() {
            StringBuilder query = new StringBuilder();

            query.append(query()).append(SPACE)
                    .append(Graql.Token.Command.GROUP).append(SPACE)
                    .append(var).append(SEMICOLON);

            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Group that = (Group) o;

            return (this.query.equals(that.query) &&
                    this.var.equals(that.var));
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public static class Aggregate extends GraqlQuery {

            private final GraqlGet.Group group;
            private final Graql.Token.Aggregate.Method method;
            private final UnscopedVariable var;
            private final int hash;

            Aggregate(GraqlGet.Group group, Graql.Token.Aggregate.Method method, UnscopedVariable var) {
                if (group == null) throw new NullPointerException("GraqlGet.Group is null");
                if (method == null) throw new NullPointerException("Method is null");
                if (var == null && !method.equals(Graql.Token.Aggregate.Method.COUNT)) {
                    throw new NullPointerException("Variable is null");
                } else if (var != null && method.equals(Graql.Token.Aggregate.Method.COUNT)) {
                    throw new IllegalArgumentException("Aggregate COUNT does not accept a Variable");
                } else if (var != null && !group.query().variables().contains(var)) {
                    throw GraqlException.variableOutOfScope(var.toString());
                }

                this.group = group;
                this.method = method;
                this.var = var;
                this.hash = Objects.hash(group, method, var);
            }

            public GraqlGet.Group group() {
                return group;
            }

            public Graql.Token.Aggregate.Method method() {
                return method;
            }

            public UnscopedVariable var() {
                return var;
            }

            @Override
            public final String toString() {
                StringBuilder query = new StringBuilder();

                query.append(group().query()).append(SPACE)
                        .append(Graql.Token.Command.GROUP).append(SPACE)
                        .append(group().var()).append(SEMICOLON).append(SPACE)
                        .append(method);

                if (var != null) query.append(SPACE).append(var);
                query.append(SEMICOLON);

                return query.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Aggregate that = (Aggregate) o;

                return (this.group.equals(that.group) &&
                        this.method.equals(that.method) &&
                        Objects.equals(this.var, that.var));
            }

            @Override
            public int hashCode() {
                return hash;
            }
        }
    }
}
