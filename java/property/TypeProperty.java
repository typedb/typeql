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
import graql.lang.statement.Label;
import graql.lang.statement.StatementType;

/**
 * Represents the {@code label} property on a Type.
 * This property can be queried and inserted. If used in an insert query and there is an existing type with the give
 * label, then that type will be retrieved.
 */
public class TypeProperty extends VarProperty {

    private final Label label;

    public TypeProperty(Label label) {
        if (label == null) {
            throw new NullPointerException("Null label");
        }
        this.label = label;
    }

    public Label label() {
        return label;
    }

    @Override
    public String keyword() {
        return Graql.Token.Property.TYPE.toString();
    }

    @Override
    public String property() {
        return label().toString();
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
    public boolean uniquelyIdentifiesConcept() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TypeProperty) {
            TypeProperty that = (TypeProperty) o;
            return (this.label.equals(that.label));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.label.hashCode();
        return h;
    }
}
