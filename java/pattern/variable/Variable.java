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

import graql.lang.pattern.constraint.Constraint;

import java.util.stream.Stream;

public abstract class Variable {

    Reference reference;

    Variable(Reference reference) {
        this.reference = reference;
    }

    public abstract Stream<? extends Constraint> properties();

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public Stream<BoundVariable> variables() {
        return properties().flatMap(Constraint::variables);
    }

    public Reference.Type type() {
        return reference.type();
    }

    public String name() {
        switch (reference.type()) {
            case NAME:
                return reference.asNamed().name();
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

    public String identifier() {
        return reference.identifier();
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

    public boolean isAnonymisedWithID() {
        return reference.isAnonymous() && reference.asAnonymous().isWithID();
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
