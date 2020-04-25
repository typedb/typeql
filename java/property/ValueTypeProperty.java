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
 * Represents the {@code valuetype} property on a AttributeType.
 * This property can be queried or inserted.
 */
public class ValueTypeProperty extends VarProperty {

    private final Graql.Token.ValueType valueType;


    public ValueTypeProperty(Graql.Token.ValueType valueType) {
        if (valueType == null) {
            throw new NullPointerException("Null valueType");
        }
        this.valueType = valueType;
    }

    public Graql.Token.ValueType valueType() {
        return valueType;
    }

    @Override
    public String keyword() {
        return Graql.Token.Property.VALUE_TYPE.toString();
    }

    @Override
    public String property() {
        return valueType.toString();
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
        if (o instanceof ValueTypeProperty) {
            ValueTypeProperty that = (ValueTypeProperty) o;
            return (this.valueType.equals(that.valueType()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.valueType.hashCode();
        return h;
    }
}
