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

import graql.lang.Graql;
import graql.lang.pattern.variable.UnscopedVariable;

import java.util.Objects;
import java.util.Optional;

public interface Filterable {

    Optional<Sorting> sort();

    Optional<Long> offset();

    Optional<Long> limit();

    default String printFilters() {
        StringBuilder filters = new StringBuilder();

        sort().ifPresent(sort -> filters.append(Graql.Token.Filter.SORT).append(Graql.Token.Char.SPACE)
                .append(sort).append(Graql.Token.Char.SEMICOLON).append(Graql.Token.Char.SPACE));

        offset().ifPresent(offset -> filters.append(Graql.Token.Filter.OFFSET).append(Graql.Token.Char.SPACE)
                .append(offset).append(Graql.Token.Char.SEMICOLON).append(Graql.Token.Char.SPACE));

        limit().ifPresent(limit -> filters.append(Graql.Token.Filter.LIMIT).append(Graql.Token.Char.SPACE)
                .append(limit).append(Graql.Token.Char.SEMICOLON).append(Graql.Token.Char.SPACE));

        return filters.toString().trim();
    }

    interface Unfiltered<S extends Sorted, O extends Offsetted, L extends Limited> extends Filterable {

        default S sort(String var) {
            return sort(UnscopedVariable.named(var));
        }

        default S sort(String var, String order) {
            Graql.Token.Order o = Graql.Token.Order.of(order);
            if (o == null) throw new IllegalArgumentException(
                    "Invalid sorting order. Valid options: '" + Graql.Token.Order.ASC + "' or '" + Graql.Token.Order.DESC
            );
            return sort(UnscopedVariable.named(var), o);
        }

        default S sort(String var, Graql.Token.Order order) {
            return sort(UnscopedVariable.named(var), order);
        }

        default S sort(UnscopedVariable var) {
            return sort(new Sorting(var));
        }

        default S sort(UnscopedVariable var, Graql.Token.Order order) {
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
        private final Graql.Token.Order order;
        private final int hash;

        public Sorting(UnscopedVariable var) {
            this(var, null);
        }

        public Sorting(UnscopedVariable var, Graql.Token.Order order) {
            this.var = var;
            this.order = order;
            this.hash = Objects.hash(var(), order());
        }

        public UnscopedVariable var() {
            return var;
        }

        public Graql.Token.Order order() {
            return order == null ? Graql.Token.Order.ASC : order;
        }

        @Override
        public String toString() {
            StringBuilder sort = new StringBuilder();

            sort.append(var);
            if (order != null) {
                sort.append(Graql.Token.Char.SPACE).append(order);
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
