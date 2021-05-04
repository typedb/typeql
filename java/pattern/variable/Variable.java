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

package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.Constraint;

import java.util.List;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class Variable {

    final Reference reference;

    Variable(Reference reference) {
        this.reference = reference;
    }

    public abstract List<? extends Constraint<?>> constraints();

    public boolean isUnbound() {
        return false;
    }

    public boolean isBound() {
        return false;
    }

    public boolean isConcept() {
        return false;
    }

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public UnboundVariable asUnbound() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(UnboundVariable.class)));
    }

    public BoundVariable asBound() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(BoundVariable.class)));
    }

    public Stream<BoundVariable> variables() {
        return constraints().stream().flatMap(constraint -> constraint.variables().stream());
    }

    public Reference.Type type() {
        return reference.type();
    }

    public String name() {
        switch (reference.type()) {
            case NAME:
                return reference.asName().name();
            case LABEL:
            case ANONYMOUS:
                return null;
            default:
                assert false;
                return null;
        }
    }

    public Reference reference() {
        return reference;
    }

    public boolean isNamed() {
        return reference.isName();
    }

    public boolean isLabelled() {
        return reference.isLabel();
    }

    public boolean isAnonymised() {
        return reference.isAnonymous();
    }

    public boolean isVisible() {
        return reference.isVisible();
    }

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
