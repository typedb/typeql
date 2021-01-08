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
 *
 */

package graql.lang.pattern;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.schema.Rule;
import graql.lang.pattern.variable.TypeVariable;

import static grakn.common.util.Objects.className;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public interface Definable {

    default boolean isRule() {
        return false;
    }

    default boolean isTypeVariable() {
        return false;
    }

    default Rule asRule() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Rule.class)));
    }

    default TypeVariable asTypeVariable() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeVariable.class)));
    }
}
