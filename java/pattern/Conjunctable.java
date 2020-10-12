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

import static grakn.common.util.Objects.className;

public interface Conjunctable extends Pattern {

    @Override
    default boolean isVariable() { return false; }

    @Override
    default boolean isNegation() { return false; }

    @Override
    default boolean isConjunctable() { return true; }

    @Override
    default BoundVariable asVariable() {
        throw GraqlException.of(ErrorMessage.INVALID_CASTING.message(className(this.getClass()), className(BoundVariable.class)));
    }

    @Override
    default Negation<? extends Pattern> asNegation() {
        throw GraqlException.of(ErrorMessage.INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    @Override
    default Conjunctable asConjunctable() {
        return this;
    }
}
