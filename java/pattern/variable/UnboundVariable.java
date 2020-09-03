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

import graql.lang.pattern.property.Property;
import graql.lang.pattern.property.ThingProperty;
import graql.lang.pattern.property.TypeProperty;
import graql.lang.pattern.variable.builder.ThingVariableBuilder;
import graql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.Set;

import static grakn.common.collection.Collections.set;

public class UnboundVariable extends Variable<UnboundVariable> implements TypeVariableBuilder,
                                                                          ThingVariableBuilder.Common<ThingBoundVariable.Thing>,
                                                                          ThingVariableBuilder.Thing,
                                                                          ThingVariableBuilder.Relation,
                                                                          ThingVariableBuilder.Attribute {

    UnboundVariable(Reference reference) {
        super(reference);
    }

    public static UnboundVariable of(Reference reference) {
        return new UnboundVariable(reference);
    }

    public static UnboundVariable named(String name) {
        return of(Reference.named(name));
    }

    public static UnboundVariable anonymous() {
        return of(Reference.anonymous(true));
    }

    public static UnboundVariable hidden() {
        return of(Reference.anonymous(false));
    }

    public TypeBoundVariable toType() {
        return new TypeBoundVariable(reference, null);
    }

    public ThingBoundVariable<?> toThing() {
        return new ThingBoundVariable.Thing(reference, null);
    }

    @Override
    public Set<Property> properties() {
        return set();
    }

    @Override
    public TypeBoundVariable asTypeWith(TypeProperty.Singular property) {
        if (!isVisible() && property instanceof TypeProperty.Label) {
            return new TypeBoundVariable(Reference.label(((TypeProperty.Label) property).scopedLabel()), property);
        } else {
            return new TypeBoundVariable(reference, property);
        }
    }

    @Override
    public TypeBoundVariable asTypeWith(TypeProperty.Repeatable property) {
        return new TypeBoundVariable(reference, property);
    }

    @Override
    public ThingBoundVariable.Thing asSameThingWith(ThingProperty.Singular property) {
        return new ThingBoundVariable.Thing(reference, property);
    }

    @Override
    public ThingBoundVariable.Thing asSameThingWith(ThingProperty.Repeatable property) {
        return new ThingBoundVariable.Thing(reference, property);
    }

    @Override
    public ThingBoundVariable.Thing asThingWith(ThingProperty.Singular property) {
        return new ThingBoundVariable.Thing(reference, property);
    }

    @Override
    public ThingBoundVariable.Attribute asAttributeWith(ThingProperty.Value<?> property) {
        return new ThingBoundVariable.Attribute(reference, property);
    }

    @Override
    public ThingBoundVariable.Relation asRelationWith(ThingProperty.Relation.RolePlayer rolePlayer) {
        return asRelationWith(new ThingProperty.Relation(rolePlayer));
    }

    public ThingBoundVariable.Relation asRelationWith(ThingProperty.Relation property) {
        return new ThingBoundVariable.Relation(reference, property);
    }

    @Override
    public String toString() {
        return reference.syntax();
    }
}
