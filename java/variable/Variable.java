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

package graql.lang.variable;

import graql.lang.exception.GraqlException;
import graql.lang.pattern.Pattern;
import graql.lang.property.Property;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class Variable implements Pattern {

    final Identity identity;

    Variable(Identity identity) {
        this.identity = identity;
    }

    public abstract Variable withoutProperties();

    public abstract List<? extends Property> properties();

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public TypeVariable asType() {
        throw GraqlException.invalidCastException(ThingVariable.class, TypeVariable.class);
    }

    public ThingVariable asThing() {
        throw GraqlException.invalidCastException(TypeVariable.class, ThingVariable.class);
    }

    public Stream<Variable> variables() {
        return properties().stream().flatMap(Property::variables);
    }

    public Identity.Type type() {
        return identity.type();
    }

    public String name() {
        switch (identity.type()) {
            case NAMED:
                return identity.asNamed().name();
            case ANONYMOUS:
                return null;
            default:
                assert false;
                return null;
        }
    }

    public String identifier() {
        return identity.identifier();
    }

    public boolean isNamed() {
        return identity.type() == Identity.Type.NAMED;
    }

    public boolean isVisible() {
        return identity.isVisible();
    }

    @Override
    public abstract String toString();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass().isAssignableFrom(Variable.class)) return false;
        Variable that = (Variable) o;
        return (this.identity.equals(that.identity) &&
                this.properties().equals(that.properties()));
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.identity, this.properties());
    }
}
