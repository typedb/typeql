/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.constraint;

import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.common.exception.TypeQLException;

import java.util.Set;

import static com.typedb.common.util.Objects.className;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class Constraint {

    public abstract Set<? extends TypeQLVariable> variables();

    public boolean isConcept() {
        return false;
    }

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public ConceptConstraint asConcept() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ConceptConstraint.class)));
    }

    public TypeConstraint asType() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeConstraint.class)));
    }

    public ThingConstraint asThing() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ThingConstraint.class)));
    }

    public ValueConstraint asValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ValueConstraint.class)));
    }

    @Override
    public abstract String toString();
}
