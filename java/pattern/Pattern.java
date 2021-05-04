/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.List;
import java.util.Set;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public interface Pattern {

    Pattern normalise();

    List<? extends Pattern> patterns();

    void validateIsBoundedBy(Set<UnboundVariable> bounds);

    default boolean isVariable() { return false; }

    default boolean isConjunction() { return false; }

    default boolean isDisjunction() { return false; }

    default boolean isNegation() { return false; }

    default boolean isConjunctable() { return false; }

    default BoundVariable asVariable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(BoundVariable.class)));
    }

    default Conjunction<? extends Pattern> asConjunction() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Conjunction.class)));
    }

    default Disjunction<? extends Pattern> asDisjunction() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Disjunction.class)));
    }

    default Negation<? extends Pattern> asNegation() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    default Conjunctable asConjunctable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    @Override
    String toString();
}
