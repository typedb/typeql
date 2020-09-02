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

package graql.lang.pattern.variable.builder;

import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.property.TypeProperty;
import graql.lang.pattern.variable.TypeBoundVariable;
import graql.lang.pattern.variable.UnboundVariable;

public interface TypeVariableBuilder {

    default TypeBoundVariable type(GraqlToken.Type type) {
        return type(type.toString());
    }

    default TypeBoundVariable type(String label) {
        return asTypeWith(new TypeProperty.Label(label));
    }

    default TypeBoundVariable type(String scope, String label) {
        return asTypeWith(new TypeProperty.Label(scope, label));
    }

    default TypeBoundVariable isAbstract() {
        return asTypeWith(new TypeProperty.Abstract());
    }

    default TypeBoundVariable sub(GraqlToken.Type type) {
        return sub(type.toString());
    }

    default TypeBoundVariable sub(String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeLabel, false));
    }

    default TypeBoundVariable sub(String typeScope, String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeScope, typeLabel, false));
    }

    default TypeBoundVariable sub(UnboundVariable typeVar) {
        return asTypeWith(new TypeProperty.Sub(typeVar, false));
    }

    default TypeBoundVariable subX(GraqlToken.Type type) {
        return subX(type.toString());
    }

    default TypeBoundVariable subX(String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeLabel, true));
    }

    default TypeBoundVariable subX(String typeScope, String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeScope, typeLabel, true));
    }

    default TypeBoundVariable subX(UnboundVariable typeVar) {
        return asTypeWith(new TypeProperty.Sub(typeVar, true));
    }

    default TypeBoundVariable owns(String attributeType) {
        return asTypeWith(new TypeProperty.Owns(attributeType, false));
    }

    default TypeBoundVariable owns(String attributeType, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeType, isKey));
    }

    default TypeBoundVariable owns(UnboundVariable attributeTypeVar) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, false));
    }

    default TypeBoundVariable owns(UnboundVariable attributeTypeVar, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, isKey));
    }

    default TypeBoundVariable owns(String attributeType, String overriddenAttributeType) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeType, false));
    }

    default TypeBoundVariable owns(String attributeType, String overriddenAttributeType, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeType, isKey));
    }

    default TypeBoundVariable owns(String attributeType, UnboundVariable overriddenAttributeTypeVar) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeTypeVar, false));
    }

    default TypeBoundVariable owns(String attributeType, UnboundVariable overriddenAttributeTypeVar, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeTypeVar, isKey));
    }

    default TypeBoundVariable owns(UnboundVariable attributeTypeVar, String overriddenAttributeType) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeType, false));
    }

    default TypeBoundVariable owns(UnboundVariable attributeTypeVar, String overriddenAttributeType, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeType, isKey));
    }

    default TypeBoundVariable owns(UnboundVariable attributeTypeVar, UnboundVariable overriddenAttributeTypeVar) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeTypeVar, false));
    }

    default TypeBoundVariable owns(UnboundVariable attributeTypeVar, UnboundVariable overriddenAttributeTypeVar, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeTypeVar, isKey));
    }

    default TypeBoundVariable plays(String relationType, String roleType) {
        return asTypeWith(new TypeProperty.Plays(relationType, roleType));
    }

    default TypeBoundVariable plays(UnboundVariable roleTypevar) {
        return asTypeWith(new TypeProperty.Plays(roleTypevar));
    }

    default TypeBoundVariable plays(String relationType, String roleType, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Plays(relationType, roleType, overriddenRoleType));
    }

    default TypeBoundVariable plays(String relationType, String roleType, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Plays(relationType, roleType, overriddenRoleTypeVar));
    }

    default TypeBoundVariable plays(UnboundVariable roleTypeVar, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Plays(roleTypeVar, overriddenRoleType));
    }

    default TypeBoundVariable plays(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Plays(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeBoundVariable relates(String roleType) {
        return asTypeWith(new TypeProperty.Relates(roleType));
    }

    default TypeBoundVariable relates(UnboundVariable roleTypeVar) {
        System.out.println(this);
        return asTypeWith(new TypeProperty.Relates(roleTypeVar));
    }

    default TypeBoundVariable relates(String roleType, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Relates(roleType, overriddenRoleType));
    }

    default TypeBoundVariable relates(String roleType, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleType, overriddenRoleTypeVar));
    }

    default TypeBoundVariable relates(UnboundVariable roleTypeVar, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar, overriddenRoleType));
    }

    default TypeBoundVariable relates(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeBoundVariable value(GraqlArg.ValueType ValueType) {
        return asTypeWith(new TypeProperty.ValueType(ValueType));
    }

    default TypeBoundVariable regex(String regex) {
        return asTypeWith(new TypeProperty.Regex(regex));
    }

    // TODO: make when() method take a more strict sub type of pattern
    default TypeBoundVariable when(Pattern when) {
        return asTypeWith(new TypeProperty.When(when));
    }

    // TODO: make then() method take a more strict sub type of pattern
    default TypeBoundVariable then(Pattern then) {
        return asTypeWith(new TypeProperty.Then(then));
    }

    TypeBoundVariable asTypeWith(TypeProperty.Singular property);

    TypeBoundVariable asTypeWith(TypeProperty.Repeatable property);
}
