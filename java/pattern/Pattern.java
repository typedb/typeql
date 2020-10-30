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

package graql.lang.pattern;

import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.BoundVariable;

import java.util.List;

import static grakn.common.util.Objects.className;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public interface Pattern {

    Pattern normalise();

    List<? extends Pattern> patterns();

    default boolean isVariable() { return false; }

    default boolean isConjunction() { return false; }

    default boolean isDisjunction() { return false; }

    default boolean isNegation() { return false; }

    default boolean isConjunctable() { return false; }

    default BoundVariable asVariable() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(BoundVariable.class)));
    }

    default Conjunction<? extends Pattern> asConjunction() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Conjunction.class)));
    }

    default Disjunction<? extends Pattern> asDisjunction() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Disjunction.class)));
    }

    default Negation<? extends Pattern> asNegation() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    default Conjunctable asConjunctable() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    @Override
    String toString();
}
