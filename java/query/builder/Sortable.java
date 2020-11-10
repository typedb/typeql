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
import graql.lang.pattern.variable.UnboundVariable;

import java.util.Objects;

public interface Sortable<S, O, L> {

    default S sort(String var) {
        return sort(UnboundVariable.named(var));
    }

    default S sort(String var, String order) {
        final GraqlArg.Order o = GraqlArg.Order.of(order);
        if (o == null) throw new IllegalArgumentException(
                "Invalid sorting order. Valid options: '" + GraqlArg.Order.ASC + "' or '" + GraqlArg.Order.DESC
        );
        return sort(UnboundVariable.named(var), o);
    }

    default S sort(String var, GraqlArg.Order order) {
        return sort(UnboundVariable.named(var), order);
    }

    default S sort(UnboundVariable var) {
        return sort(new Sorting(var));
    }

    default S sort(UnboundVariable var, GraqlArg.Order order) {
        return sort(new Sorting(var, order));
    }

    S sort(Sorting sorting);

    O offset(long offset);

    L limit(long limit);

    class Sorting {

        private final UnboundVariable var;
        private final GraqlArg.Order order;
        private final int hash;

        public Sorting(UnboundVariable var) {
            this(var, null);
        }

        public Sorting(UnboundVariable var, GraqlArg.Order order) {
            this.var = var;
            this.order = order;
            this.hash = Objects.hash(var(), order());
        }

        public UnboundVariable var() {
            return var;
        }

        public GraqlArg.Order order() {
            return order == null ? GraqlArg.Order.ASC : order;
        }

        @Override
        public String toString() {
            final StringBuilder sort = new StringBuilder();

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

            final Sorting that = (Sorting) o;

            return (this.var().equals(that.var()) &&
                    this.order().equals(that.order()));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
