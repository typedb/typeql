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

package graql.lang.query;

import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.query.builder.Aggregatable;
import graql.lang.query.builder.Sortable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.NEW_LINE;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Command.GROUP;
import static graql.lang.common.GraqlToken.Filter.GET;
import static graql.lang.common.GraqlToken.Filter.LIMIT;
import static graql.lang.common.GraqlToken.Filter.OFFSET;
import static graql.lang.common.GraqlToken.Filter.SORT;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_FILTER_VARIABLE_REPEATING;
import static graql.lang.common.exception.ErrorMessage.INVALID_COUNT_VARIABLE_ARGUMENT;
import static graql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE;
import static graql.lang.common.exception.ErrorMessage.MATCH_HAS_NO_NAMED_VARIABLE;
import static graql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static graql.lang.common.exception.ErrorMessage.VARIABLE_NOT_NAMED;
import static graql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_MATCH;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class GraqlMatch extends GraqlQuery implements Aggregatable<GraqlMatch.Aggregate> {

    private final Conjunction<? extends Pattern> conjunction;
    private final Modifier modifier;

    private final int hash;

    private List<BoundVariable> variables;
    private List<UnboundVariable> variablesNamedUnbound;

    GraqlMatch(Conjunction<? extends Pattern> conjunction) {
        this(conjunction, new ArrayList<>());
    }

    GraqlMatch(Conjunction<? extends Pattern> conjunction, List<UnboundVariable> filter) {
        this(conjunction, filter, null, null, null);
    }

    // We keep this constructor 'public' as it is more efficient for query parsing
    public GraqlMatch(Conjunction<? extends Pattern> conjunction, List<UnboundVariable> filter, Sortable.Sorting sorting, Long offset, Long limit) {
        if (filter == null) throw GraqlException.of(ErrorMessage.MISSING_MATCH_FILTER.message());
        this.conjunction = conjunction;
        this.modifier = new Modifier(filter, sorting, offset, limit);

        hasBoundingConjunction();
        nestedPatternsAreBounded();
        hasNamedVariable();
        filtersAreInScope();
        sortVarsArInScope();

        this.hash = Objects.hash(this.conjunction, this.modifier);
    }

    public static class Modifier {

        private final List<UnboundVariable> filter;
        private final Sortable.Sorting sorting;
        private final Long offset;
        private final Long limit;

        private final int hash;

        public Modifier(List<UnboundVariable> filter, @Nullable Sortable.Sorting sorting, @Nullable Long offset,
                        @Nullable Long limit) {
            this.filter = list(filter);
            this.sorting = sorting;
            this.offset = offset;
            this.limit = limit;
            this.hash = Objects.hash(this.filter, this.sorting, this.offset, this.limit);
        }

        public List<UnboundVariable> filter() {
            return filter;
        }

        public boolean isEmpty() {
            return filter.isEmpty() && sorting == null && offset == null && limit == null;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            if (!filter.isEmpty()) {
                syntax.append(GET);
                final String varsStr = filter.stream().map(UnboundVariable::toString).collect(joining(COMMA_SPACE.toString()));
                syntax.append(SPACE).append(varsStr);
                syntax.append(SEMICOLON);
            }
            if (sorting != null) syntax.append(SORT).append(SPACE).append(sorting).append(SEMICOLON).append(SPACE);
            if (offset != null) syntax.append(OFFSET).append(SPACE).append(offset).append(SEMICOLON).append(SPACE);
            if (limit != null) syntax.append(LIMIT).append(SPACE).append(limit).append(SEMICOLON).append(SPACE);
            return syntax.toString();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Modifier modifier = (Modifier) o;
            return Objects.equals(filter, modifier.filter) && Objects.equals(sorting, modifier.sorting)
                    && Objects.equals(offset, modifier.offset) && Objects.equals(limit, modifier.limit);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    private void hasBoundingConjunction() {
        if (!conjunction.namedVariablesUnbound().findAny().isPresent()) {
            throw GraqlException.of(MATCH_HAS_NO_BOUNDING_NAMED_VARIABLE);
        }
    }

    private void nestedPatternsAreBounded() {
        conjunction.patterns().stream().filter(pattern -> !pattern.isVariable()).forEach(pattern -> {
            pattern.validateIsBoundedBy(conjunction.namedVariablesUnbound().collect(toSet()));
        });
    }

    private void hasNamedVariable() {
        if (namedVariablesUnbound().isEmpty()) throw GraqlException.of(MATCH_HAS_NO_NAMED_VARIABLE);
    }

    private void filtersAreInScope() {
        Set<UnboundVariable> duplicates = new HashSet<>();
        for (UnboundVariable var : modifier.filter) {
            if (!namedVariablesUnbound().contains(var)) throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var));
            if (!var.isNamed()) throw GraqlException.of(VARIABLE_NOT_NAMED.message(var));
            if (duplicates.contains(var)) throw GraqlException.of(ILLEGAL_FILTER_VARIABLE_REPEATING);
            else duplicates.add(var);
        }
    }

    private void sortVarsArInScope() {
        final List<UnboundVariable> sortableVars = modifier.filter.isEmpty() ? namedVariablesUnbound() : modifier.filter;
        if (modifier.sorting != null && !sortableVars.contains(modifier.sorting.var())) {
            throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(modifier.sorting.var()));
        }
    }

    @Override
    public Aggregate aggregate(GraqlToken.Aggregate.Method method, UnboundVariable var) {
        return new Aggregate(this, method, var);
    }

    public Group group(String var) {
        return group(UnboundVariable.named(var));
    }

    public Group group(UnboundVariable var) {
        return new Group(this, var);
    }

    @Override
    public GraqlArg.QueryType type() {
        return GraqlArg.QueryType.READ;
    }

    public Conjunction<? extends Pattern> conjunction() {
        return conjunction;
    }

    public List<BoundVariable> variables() {
        if (variables == null) variables = conjunction.variables().collect(toList());
        return variables;
    }

    public List<UnboundVariable> namedVariablesUnbound() {
        if (variablesNamedUnbound == null) {
            variablesNamedUnbound = conjunction.namedVariablesUnbound().collect(toList());
        }
        return variablesNamedUnbound;
    }

    public Modifier modifier() {
        return modifier;
    }

    @Override
    public String toString() {
        final StringBuilder query = new StringBuilder();
        query.append(GraqlToken.Command.MATCH);

        if (conjunction.patterns().size() > 1) query.append(NEW_LINE);
        else query.append(SPACE);
        query.append(conjunction.patterns().stream().map(Object::toString).collect(joining("" + SEMICOLON + NEW_LINE)));
        query.append(SEMICOLON);

        if (!modifier.isEmpty()) {
            if (conjunction.patterns().size() > 1) query.append(NEW_LINE);
            else query.append(SPACE);
            query.append(modifier.toString());
        }

        return query.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }
        final GraqlMatch that = (GraqlMatch) o;
        return Objects.equals(this.conjunction, that.conjunction) && Objects.equals(this.modifier, that.modifier);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Unfiltered extends GraqlMatch implements Sortable<Sorted, Offset, Limited> {

        public Unfiltered(List<? extends Pattern> patterns) {
            super(validConjunction(patterns));
        }

        static Conjunction<? extends Pattern> validConjunction(List<? extends Pattern> patterns) {
            if (patterns.size() == 0) throw GraqlException.of(MISSING_PATTERNS.message());
            return new Conjunction<>(patterns);
        }

        public GraqlMatch.Filtered get(String var, String... vars) {
            return get(concat(of(var), of(vars)).map(UnboundVariable::named).collect(toList()));
        }

        public GraqlMatch.Filtered get(UnboundVariable var, UnboundVariable... vars) {
            final List<UnboundVariable> varList = new ArrayList<>();
            varList.add(var);
            varList.addAll(Arrays.asList(vars));
            return get(varList);
        }

        public GraqlMatch.Filtered get(List<UnboundVariable> vars) {
            return new GraqlMatch.Filtered(this, vars);
        }

        @Override
        public GraqlMatch.Sorted sort(Sorting sorting) {
            return new GraqlMatch.Sorted(this, sorting);
        }

        @Override
        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        @Override
        public GraqlMatch.Limited limit(long limit) {
            return new GraqlMatch.Limited(this, limit);
        }

        public GraqlInsert insert(ThingVariable<?>... things) {
            return insert(list(things));
        }

        public GraqlInsert insert(List<ThingVariable<?>> things) {
            return new GraqlInsert(this, things);
        }

        public GraqlDelete delete(ThingVariable<?>... things) {
            return delete(list(things));
        }

        public GraqlDelete delete(List<ThingVariable<?>> things) {
            return new GraqlDelete(this, things);
        }
    }

    public static class Filtered extends GraqlMatch implements Sortable<Sorted, Offset, Limited> {

        Filtered(Unfiltered unfiltered, List<UnboundVariable> filter) {
            super(unfiltered.conjunction(), filter, null, null, null);
            if (filter.isEmpty()) throw GraqlException.of(ErrorMessage.EMPTY_MATCH_FILTER);
        }

        @Override
        public GraqlMatch.Sorted sort(Sorting sorting) {
            return new GraqlMatch.Sorted(this, sorting);
        }

        @Override
        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        @Override
        public GraqlMatch.Limited limit(long limit) {
            return new GraqlMatch.Limited(this, limit);
        }
    }

    public static class Sorted extends GraqlMatch {

        Sorted(GraqlMatch match, Sortable.Sorting sorting) {
            super(match.conjunction, match.modifier.filter, sorting, match.modifier.offset, match.modifier.limit);
        }

        public Offset offset(long offset) {
            return new Offset(this, offset);
        }

        public GraqlMatch.Limited limit(long limit) {
            return new GraqlMatch.Limited(this, limit);
        }
    }

    public static class Offset extends GraqlMatch {

        Offset(GraqlMatch match, long offset) {
            super(match.conjunction, match.modifier.filter, match.modifier.sorting, offset, match.modifier.limit);
        }

        public GraqlMatch.Limited limit(long limit) {
            return new GraqlMatch.Limited(this, limit);
        }
    }

    public static class Limited extends GraqlMatch {

        Limited(GraqlMatch match, long limit) {
            super(match.conjunction, match.modifier.filter, match.modifier.sorting, match.modifier.offset, limit);
        }
    }

    public static class Aggregate extends GraqlQuery {

        private final GraqlMatch query;
        private final GraqlToken.Aggregate.Method method;
        private final UnboundVariable var;
        private final int hash;

        Aggregate(GraqlMatch query, GraqlToken.Aggregate.Method method, UnboundVariable var) {
            if (query == null) throw new NullPointerException("MatchQuery is null");
            if (method == null) throw new NullPointerException("Method is null");

            if (var == null && !method.equals(GraqlToken.Aggregate.Method.COUNT)) {
                throw new NullPointerException("Variable is null");
            } else if (var != null && method.equals(GraqlToken.Aggregate.Method.COUNT)) {
                throw GraqlException.of(INVALID_COUNT_VARIABLE_ARGUMENT.message());
            } else if (var != null && !query.modifier().filter().contains(var)) {
                throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var.toString()));
            }

            this.query = query;
            this.method = method;
            this.var = var;
            this.hash = Objects.hash(query, method, var);
        }

        @Override
        public GraqlArg.QueryType type() {
            return GraqlArg.QueryType.READ;
        }

        public GraqlMatch match() {
            return query;
        }

        public GraqlToken.Aggregate.Method method() {
            return method;
        }

        public UnboundVariable var() {
            return var;
        }

        @Override
        public final String toString() {
            final StringBuilder query = new StringBuilder();

            if (match().modifier().filter().isEmpty() && match().conjunction().patterns().size() > 1) {
                query.append(match()).append(NEW_LINE);
            } else query.append(match()).append(SPACE);

            query.append(method);
            if (var != null) query.append(SPACE).append(var);
            query.append(SEMICOLON);

            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Aggregate that = (Aggregate) o;
            return this.query.equals(that.query) && this.method.equals(that.method) && Objects.equals(this.var, that.var);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Group extends GraqlQuery implements Aggregatable<Group.Aggregate> {

        private final GraqlMatch query;
        private final UnboundVariable var;
        private final int hash;

        Group(GraqlMatch query, UnboundVariable var) {
            if (query == null) throw new NullPointerException("GetQuery is null");
            if (var == null) throw new NullPointerException("Variable is null");
            else if (!query.modifier().filter().contains(var)) {
                throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var.toString()));
            }

            this.query = query;
            this.var = var;
            this.hash = Objects.hash(query, var);
        }

        @Override
        public GraqlArg.QueryType type() {
            return GraqlArg.QueryType.READ;
        }

        public GraqlMatch match() {
            return query;
        }

        public UnboundVariable var() {
            return var;
        }

        @Override
        public Aggregate aggregate(GraqlToken.Aggregate.Method method, UnboundVariable var) {
            return new Aggregate(this, method, var);
        }

        @Override
        public String toString() {
            final StringBuilder query = new StringBuilder();

            if (match().modifier().filter.isEmpty() && match().conjunction().patterns().size() > 1) {
                query.append(match()).append(NEW_LINE);
            } else query.append(match()).append(SPACE);

            query.append(GROUP).append(SPACE).append(var).append(SEMICOLON);
            return query.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Group that = (Group) o;
            return this.query.equals(that.query) && this.var.equals(that.var);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public static class Aggregate extends GraqlQuery {

            private final GraqlMatch.Group group;
            private final GraqlToken.Aggregate.Method method;
            private final UnboundVariable var;
            private final int hash;

            Aggregate(GraqlMatch.Group group, GraqlToken.Aggregate.Method method, UnboundVariable var) {
                if (group == null) throw new NullPointerException("GraqlGet.Group is null");
                if (method == null) throw new NullPointerException("Method is null");
                if (var == null && !method.equals(GraqlToken.Aggregate.Method.COUNT)) {
                    throw new NullPointerException("Variable is null");
                } else if (var != null && method.equals(GraqlToken.Aggregate.Method.COUNT)) {
                    throw new IllegalArgumentException(INVALID_COUNT_VARIABLE_ARGUMENT.message());
                } else if (var != null && !group.match().modifier().filter().contains(var)) {
                    throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_MATCH.message(var.toString()));
                }

                this.group = group;
                this.method = method;
                this.var = var;
                this.hash = Objects.hash(group, method, var);
            }

            @Override
            public GraqlArg.QueryType type() {
                return GraqlArg.QueryType.READ;
            }

            public GraqlMatch.Group group() {
                return group;
            }

            public GraqlToken.Aggregate.Method method() {
                return method;
            }

            public UnboundVariable var() {
                return var;
            }

            @Override
            public final String toString() {
                final StringBuilder query = new StringBuilder();

                if (group().match().modifier().filter.isEmpty() && group().match().conjunction().patterns().size() > 1) {
                    query.append(group().match()).append(NEW_LINE);
                } else query.append(group().match()).append(SPACE);

                query.append(GROUP).append(SPACE).append(group().var()).append(SEMICOLON).append(SPACE).append(method);

                if (var != null) query.append(SPACE).append(var);
                query.append(SEMICOLON);

                return query.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                final Aggregate that = (Aggregate) o;
                return this.group.equals(that.group) && this.method.equals(that.method)
                        && Objects.equals(this.var, that.var);
            }

            @Override
            public int hashCode() {
                return hash;
            }
        }
    }
}
