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


package graql.lang.query.builder;

import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.pattern.variable.UnscopedVariable;

import java.util.Objects;
import java.util.Optional;

public interface Filterable {

    Optional<Sorting> sort();

    Optional<Long> offset();

    Optional<Long> limit();

    default String printFilters() {
        StringBuilder filters = new StringBuilder();

        sort().ifPresent(sort -> filters.append(GraqlToken.Filter.SORT).append(GraqlToken.Char.SPACE)
                .append(sort).append(GraqlToken.Char.SEMICOLON).append(GraqlToken.Char.SPACE));

        offset().ifPresent(offset -> filters.append(GraqlToken.Filter.OFFSET).append(GraqlToken.Char.SPACE)
                .append(offset).append(GraqlToken.Char.SEMICOLON).append(GraqlToken.Char.SPACE));

        limit().ifPresent(limit -> filters.append(GraqlToken.Filter.LIMIT).append(GraqlToken.Char.SPACE)
                .append(limit).append(GraqlToken.Char.SEMICOLON).append(GraqlToken.Char.SPACE));

        return filters.toString().trim();
    }

    interface Unfiltered<S extends Sorted, O extends Offsetted, L extends Limited> extends Filterable {

        default S sort(String var) {
            return sort(UnscopedVariable.named(var));
        }

        default S sort(String var, String order) {
            GraqlArg.Order o = GraqlArg.Order.of(order);
            if (o == null) throw new IllegalArgumentException(
                    "Invalid sorting order. Valid options: '" + GraqlArg.Order.ASC + "' or '" + GraqlArg.Order.DESC
            );
            return sort(UnscopedVariable.named(var), o);
        }

        default S sort(String var, GraqlArg.Order order) {
            return sort(UnscopedVariable.named(var), order);
        }

        default S sort(UnscopedVariable var) {
            return sort(new Sorting(var));
        }

        default S sort(UnscopedVariable var, GraqlArg.Order order) {
            return sort(new Sorting(var, order));
        }

        S sort(Sorting sorting);

        O offset(long offset);

        L limit(long limit);
    }

    interface Sorted<O extends Offsetted, L extends Limited> extends Filterable {

        O offset(long offset);

        L limit(long limit);
    }

    interface Offsetted<L extends Limited> extends Filterable {

        L limit(long limit);
    }

    interface Limited extends Filterable {

    }

    class Sorting {

        private final UnscopedVariable var;
        private final GraqlArg.Order order;
        private final int hash;

        public Sorting(UnscopedVariable var) {
            this(var, null);
        }

        public Sorting(UnscopedVariable var, GraqlArg.Order order) {
            this.var = var;
            this.order = order;
            this.hash = Objects.hash(var(), order());
        }

        public UnscopedVariable var() {
            return var;
        }

        public GraqlArg.Order order() {
            return order == null ? GraqlArg.Order.ASC : order;
        }

        @Override
        public String toString() {
            StringBuilder sort = new StringBuilder();

            sort.append(var);
            if (order != null) {
                sort.append(GraqlToken.Char.SPACE).append(order);
            }

            return sort.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Sorting that = (Sorting) o;

            return (this.var().equals(that.var()) &&
                    this.order().equals(that.order()));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
