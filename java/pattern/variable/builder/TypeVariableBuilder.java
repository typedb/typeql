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
import graql.lang.pattern.constraint.TypeConstraint;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;

public interface TypeVariableBuilder {

    default TypeVariable type(final GraqlToken.Type type) {
        return type(type.toString());
    }

    default TypeVariable type(final String label) {
        return constrain(new TypeConstraint.Label(label));
    }

    default TypeVariable type(final String scope, final String label) {
        return constrain(new TypeConstraint.Label(scope, label));
    }

    default TypeVariable isAbstract() {
        return constrain(new TypeConstraint.Abstract());
    }

    default TypeVariable sub(final GraqlToken.Type type) {
        return sub(type.toString());
    }

    default TypeVariable sub(final String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeLabel, false));
    }

    default TypeVariable sub(final String typeScope, final String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeScope, typeLabel, false));
    }

    default TypeVariable sub(final UnboundVariable typeVar) {
        return constrain(new TypeConstraint.Sub(typeVar, false));
    }

    default TypeVariable subX(final GraqlToken.Type type) {
        return subX(type.toString());
    }

    default TypeVariable subX(final String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeLabel, true));
    }

    default TypeVariable subX(final String typeScope, final String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeScope, typeLabel, true));
    }

    default TypeVariable subX(final UnboundVariable typeVar) {
        return constrain(new TypeConstraint.Sub(typeVar, true));
    }

    default TypeVariable owns(final String attributeType) {
        return constrain(new TypeConstraint.Owns(attributeType, false));
    }

    default TypeVariable owns(final String attributeType, final boolean isKey) {
        return constrain(new TypeConstraint.Owns(attributeType, isKey));
    }

    default TypeVariable owns(final UnboundVariable attributeTypeVar) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, false));
    }

    default TypeVariable owns(final UnboundVariable attributeTypeVar, final boolean isKey) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, isKey));
    }

    default TypeVariable owns(final String attributeType, final String overriddenAttributeType) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeType, false));
    }

    default TypeVariable owns(final String attributeType, final String overriddenAttributeType, final boolean isKey) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeType, isKey));
    }

    default TypeVariable owns(final String attributeType, final UnboundVariable overriddenAttributeTypeVar) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeTypeVar, false));
    }

    default TypeVariable owns(final String attributeType, final UnboundVariable overriddenAttributeTypeVar, final boolean isKey) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeTypeVar, isKey));
    }

    default TypeVariable owns(final UnboundVariable attributeTypeVar, final String overriddenAttributeType) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeType, false));
    }

    default TypeVariable owns(final UnboundVariable attributeTypeVar, final String overriddenAttributeType, final boolean isKey) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeType, isKey));
    }

    default TypeVariable owns(final UnboundVariable attributeTypeVar, final UnboundVariable overriddenAttributeTypeVar) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeTypeVar, false));
    }

    default TypeVariable owns(final UnboundVariable attributeTypeVar, final UnboundVariable overriddenAttributeTypeVar, final boolean isKey) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeTypeVar, isKey));
    }

    default TypeVariable plays(final String relationType, final String roleType) {
        return constrain(new TypeConstraint.Plays(relationType, roleType));
    }

    default TypeVariable plays(final UnboundVariable roleTypevar) {
        return constrain(new TypeConstraint.Plays(roleTypevar));
    }

    default TypeVariable plays(final String relationType, final String roleType, final String overriddenRoleType) {
        return constrain(new TypeConstraint.Plays(relationType, roleType, overriddenRoleType));
    }

    default TypeVariable plays(final String relationType, final String roleType, final UnboundVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Plays(relationType, roleType, overriddenRoleTypeVar));
    }

    default TypeVariable plays(final UnboundVariable roleTypeVar, final String overriddenRoleType) {
        return constrain(new TypeConstraint.Plays(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable plays(final UnboundVariable roleTypeVar, final UnboundVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Plays(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable relates(final String roleType) {
        return constrain(new TypeConstraint.Relates(roleType));
    }

    default TypeVariable relates(final UnboundVariable roleTypeVar) {
        System.out.println(this);
        return constrain(new TypeConstraint.Relates(roleTypeVar));
    }

    default TypeVariable relates(final String roleType, final String overriddenRoleType) {
        return constrain(new TypeConstraint.Relates(roleType, overriddenRoleType));
    }

    default TypeVariable relates(final String roleType, final UnboundVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleType, overriddenRoleTypeVar));
    }

    default TypeVariable relates(final UnboundVariable roleTypeVar, final String overriddenRoleType) {
        return constrain(new TypeConstraint.Relates(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable relates(final UnboundVariable roleTypeVar, final UnboundVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable value(final GraqlArg.ValueType ValueType) {
        return constrain(new TypeConstraint.ValueType(ValueType));
    }

    default TypeVariable regex(final String regex) {
        return constrain(new TypeConstraint.Regex(regex));
    }

    TypeVariable constrain(TypeConstraint.Label constraint);

    TypeVariable constrain(TypeConstraint.Sub constraint);

    TypeVariable constrain(TypeConstraint.Abstract constraint);

    TypeVariable constrain(TypeConstraint.ValueType constraint);

    TypeVariable constrain(TypeConstraint.Regex constraint);

    TypeVariable constrain(TypeConstraint.Owns constraint);

    TypeVariable constrain(TypeConstraint.Plays constraint);

    TypeVariable constrain(TypeConstraint.Relates constraint);
}
