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
import graql.lang.pattern.property.Property;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static graql.lang.common.exception.ErrorMessage.INVALID_CAST_EXCEPTION;

public abstract class Variable<T extends Variable<T>> {

    Reference reference;

    Variable(Reference reference) {
        this.reference = reference;
    }

    public abstract Set<? extends Property> properties();

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public TypeVariable toType() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Variable.class.getCanonicalName(), TypeVariable.class.getCanonicalName()
        ));
    }

    public ThingVariable<?> toThing() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Variable.class.getCanonicalName(), ThingVariable.class.getCanonicalName()
        ));
    }

    public Stream<BoundVariable<?>> variables() {
        return properties().stream().flatMap(Property::variables);
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
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass().isAssignableFrom(Variable.class)) return false;
        Variable<?> that = (Variable<?>) o;
        return (this.reference.equals(that.reference) &&
                this.properties().equals(that.properties()));
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.reference, this.properties());
    }
}
