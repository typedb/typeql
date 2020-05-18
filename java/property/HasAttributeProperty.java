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

package graql.lang.property;

import graql.lang.Graql;
import graql.lang.statement.Statement;
import graql.lang.statement.StatementInstance;
import graql.lang.statement.Variable;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Represents the {@code has} property on an Thing. This property can be queried, inserted or deleted.
 * The property is defined as a Relation between an Thing and a Attribute,
 * where theAttribute is of a particular type. When matching,  Schema.EdgeLabel#ROLE_PLAYER
 * edges are used to speed up the traversal. The type of the Relation does not matter.
 * When inserting, an implicit Relation is created between the instance and the Attribute,
 * using type labels derived from the label of the AttributeType.
 */
public class HasAttributeProperty extends VarProperty {

    private final String type;
    private final Statement attribute;

    public HasAttributeProperty(String type, Statement attribute) {
        attribute = attribute.isa(Graql.type(type));
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        this.type = type;
        this.attribute = attribute;
    }

    public String type() {
        return type;
    }

    public Statement attribute() {
        return attribute;
    }

    @Override
    public String keyword() {
        return Graql.Token.Property.HAS.toString();
    }

    @Override
    public String property() {
        Stream.Builder<String> property = Stream.builder();

        property.add(type);

        if (attribute().var().isReturned()) {
            property.add(attribute().var().toString());
        } else {
            attribute().getProperties(ValueProperty.class).forEach(prop -> property.add(prop.operation().toString()));
        }

        return property.build().collect(joining(Graql.Token.Char.SPACE.toString()));
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public Stream<Statement> types() {
        return Stream.of(Graql.type(type()));
    }

    @Override
    public Stream<Statement> statements() {
        return Stream.of(attribute());
    }

    @Override
    public Class statementClass() {
        return StatementInstance.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HasAttributeProperty that = (HasAttributeProperty) o;

        if (!type().equals(that.type())) return false;
        if (!attribute().equals(that.attribute())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type().hashCode();
        result = 31 * result + attribute().hashCode();
        return result;
    }
}
