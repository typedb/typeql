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

import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;

import static com.vaticle.typedb.common.util.Objects.className;

public interface Conjunctable extends Pattern {

    @Override
    default boolean isVariable() { return false; }

    @Override
    default boolean isNegation() { return false; }

    @Override
    default boolean isConjunctable() { return true; }

    @Override
    default BoundVariable asVariable() {
        throw TypeQLException.of(ErrorMessage.INVALID_CASTING.message(className(this.getClass()), className(BoundVariable.class)));
    }

    @Override
    default Negation<? extends Pattern> asNegation() {
        throw TypeQLException.of(ErrorMessage.INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    @Override
    default Conjunctable asConjunctable() {
        return this;
    }
}
