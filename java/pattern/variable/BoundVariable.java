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

package graql.lang.pattern.variable;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Pattern;

import static grakn.common.util.Objects.className;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class BoundVariable extends Variable implements Pattern {

    BoundVariable(Reference reference) {
        super(reference);
    }

    public UnboundVariable toUnbound() {
        return new UnboundVariable(reference);
    }

    public TypeVariable asType() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeVariable.class)));
    }

    public ThingVariable<?> asThing() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(ThingVariable.class)));
    }
}
