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
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;

public interface TypeVariableBuilder {

    default TypeVariable type(GraqlToken.Type type) {
        return type(type.toString());
    }

    default TypeVariable type(String label) {
        return asTypeWith(new TypeProperty.Label(label));
    }

    default TypeVariable type(String scope, String label) {
        return asTypeWith(new TypeProperty.Label(scope, label));
    }

    default TypeVariable isAbstract() {
        return asTypeWith(new TypeProperty.Abstract());
    }

    default TypeVariable sub(GraqlToken.Type type) {
        return sub(type.toString());
    }

    default TypeVariable sub(String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeLabel, false));
    }

    default TypeVariable sub(String typeScope, String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeScope, typeLabel, false));
    }

    default TypeVariable sub(UnboundVariable typeVar) {
        return asTypeWith(new TypeProperty.Sub(typeVar, false));
    }

    default TypeVariable subX(GraqlToken.Type type) {
        return subX(type.toString());
    }

    default TypeVariable subX(String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeLabel, true));
    }

    default TypeVariable subX(String typeScope, String typeLabel) {
        return asTypeWith(new TypeProperty.Sub(typeScope, typeLabel, true));
    }

    default TypeVariable subX(UnboundVariable typeVar) {
        return asTypeWith(new TypeProperty.Sub(typeVar, true));
    }

    default TypeVariable owns(String attributeType) {
        return asTypeWith(new TypeProperty.Owns(attributeType, false));
    }

    default TypeVariable owns(String attributeType, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeType, isKey));
    }

    default TypeVariable owns(UnboundVariable attributeTypeVar) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, false));
    }

    default TypeVariable owns(UnboundVariable attributeTypeVar, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, isKey));
    }

    default TypeVariable owns(String attributeType, String overriddenAttributeType) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeType, false));
    }

    default TypeVariable owns(String attributeType, String overriddenAttributeType, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeType, isKey));
    }

    default TypeVariable owns(String attributeType, UnboundVariable overriddenAttributeTypeVar) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeTypeVar, false));
    }

    default TypeVariable owns(String attributeType, UnboundVariable overriddenAttributeTypeVar, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeType, overriddenAttributeTypeVar, isKey));
    }

    default TypeVariable owns(UnboundVariable attributeTypeVar, String overriddenAttributeType) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeType, false));
    }

    default TypeVariable owns(UnboundVariable attributeTypeVar, String overriddenAttributeType, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeType, isKey));
    }

    default TypeVariable owns(UnboundVariable attributeTypeVar, UnboundVariable overriddenAttributeTypeVar) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeTypeVar, false));
    }

    default TypeVariable owns(UnboundVariable attributeTypeVar, UnboundVariable overriddenAttributeTypeVar, boolean isKey) {
        return asTypeWith(new TypeProperty.Owns(attributeTypeVar, overriddenAttributeTypeVar, isKey));
    }

    default TypeVariable plays(String relationType, String roleType) {
        return asTypeWith(new TypeProperty.Plays(relationType, roleType));
    }

    default TypeVariable plays(UnboundVariable roleTypevar) {
        return asTypeWith(new TypeProperty.Plays(roleTypevar));
    }

    default TypeVariable plays(String relationType, String roleType, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Plays(relationType, roleType, overriddenRoleType));
    }

    default TypeVariable plays(String relationType, String roleType, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Plays(relationType, roleType, overriddenRoleTypeVar));
    }

    default TypeVariable plays(UnboundVariable roleTypeVar, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Plays(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable plays(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Plays(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable relates(String roleType) {
        return asTypeWith(new TypeProperty.Relates(roleType));
    }

    default TypeVariable relates(UnboundVariable roleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar));
    }

    default TypeVariable relates(String roleType, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Relates(roleType, overriddenRoleType));
    }

    default TypeVariable relates(String roleType, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleType, overriddenRoleTypeVar));
    }

    default TypeVariable relates(UnboundVariable roleTypeVar, String overriddenRoleType) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable relates(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
        return asTypeWith(new TypeProperty.Relates(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable value(GraqlArg.ValueType ValueType) {
        return asTypeWith(new TypeProperty.ValueType(ValueType));
    }

    default TypeVariable regex(String regex) {
        return asTypeWith(new TypeProperty.Regex(regex));
    }

    // TODO: make when() method take a more strict sub type of pattern
    default TypeVariable when(Pattern when) {
        return asTypeWith(new TypeProperty.When(when));
    }

    // TODO: make then() method take a more strict sub type of pattern
    default TypeVariable then(Pattern then) {
        return asTypeWith(new TypeProperty.Then(then));
    }

    TypeVariable asTypeWith(TypeProperty.Singular property);

    TypeVariable asTypeWith(TypeProperty.Repeatable property);
}
