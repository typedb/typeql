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
import graql.lang.statement.StatementType;

/**
 * Represents the {@code ValueClass} property on a AttributeType.
 * This property can be queried or inserted.
 */
public class ValueClassProperty extends VarProperty {

    private final Graql.Token.ValueClass valueClass;


    public ValueClassProperty(Graql.Token.ValueClass valueClass) {
        if (valueClass == null) {
            throw new NullPointerException("Null ValueClass");
        }
        this.valueClass = valueClass;
    }

    public Graql.Token.ValueClass valueClass() {
        return valueClass;
    }

    @Override
    public String keyword() {
        return Graql.Token.Property.VALUE_CLASS.toString();
    }

    @Override
    public String property() {
        return valueClass.toString();
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public Class statementClass() {
        return StatementType.class;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ValueClassProperty) {
            ValueClassProperty that = (ValueClassProperty) o;
            return (this.valueClass.equals(that.valueClass()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.valueClass.hashCode();
        return h;
    }
}
