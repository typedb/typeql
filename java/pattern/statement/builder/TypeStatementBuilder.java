/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern.statement.builder;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.statement.TypeStatement;

public interface TypeStatementBuilder {

    default TypeStatement type(TypeQLToken.Type type) {
        return type(type.toString());
    }

    default TypeStatement type(String label) {
        return constrain(new TypeConstraint.Label(label));
    }

    default TypeStatement type(String scope, String label) {
        return constrain(new TypeConstraint.Label(scope, label));
    }

    default TypeStatement isAbstract() {
        return constrain(new TypeConstraint.Abstract());
    }

    default TypeStatement sub(TypeQLToken.Type type) {
        return sub(type.toString());
    }

    default TypeStatement sub(String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeLabel, false));
    }

    default TypeStatement sub(String typeScope, String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeScope, typeLabel, false));
    }

    default TypeStatement sub(TypeQLVariable.Concept typeVar) {
        return constrain(new TypeConstraint.Sub(typeVar, false));
    }

    default TypeStatement subX(TypeQLToken.Type type) {
        return subX(type.toString());
    }

    default TypeStatement subX(String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeLabel, true));
    }

    default TypeStatement subX(String typeScope, String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeScope, typeLabel, true));
    }

    default TypeStatement subX(TypeQLVariable.Concept typeVar) {
        return constrain(new TypeConstraint.Sub(typeVar, true));
    }

    default TypeStatement owns(String attributeType, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeType, annotations));
    }

    default TypeStatement owns(TypeQLVariable.Concept attributeTypeVar, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, annotations));
    }


    default TypeStatement owns(String attributeType, String overriddenAttributeType, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeType, annotations));
    }

    default TypeStatement owns(String attributeType, TypeQLVariable.Concept overriddenAttributeTypeVar, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeTypeVar, annotations));
    }

    default TypeStatement owns(TypeQLVariable.Concept attributeTypeVar, String overriddenAttributeType, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeType, annotations));
    }

    default TypeStatement owns(TypeQLVariable.Concept attributeTypeVar, TypeQLVariable.Concept overriddenAttributeTypeVar, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeTypeVar, annotations));
    }

    default TypeStatement plays(String relationType, String roleType) {
        return constrain(new TypeConstraint.Plays(relationType, roleType));
    }

    default TypeStatement plays(TypeQLVariable.Concept roleTypevar) {
        return constrain(new TypeConstraint.Plays(roleTypevar));
    }

    default TypeStatement plays(String relationType, String roleType, String overriddenRoleType) {
        return constrain(new TypeConstraint.Plays(relationType, roleType, overriddenRoleType));
    }

    default TypeStatement plays(String relationType, String roleType, TypeQLVariable.Concept overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Plays(relationType, roleType, overriddenRoleTypeVar));
    }

    default TypeStatement plays(TypeQLVariable.Concept roleTypeVar, String overriddenRoleType) {
        return constrain(new TypeConstraint.Plays(roleTypeVar, overriddenRoleType));
    }

    default TypeStatement plays(TypeQLVariable.Concept roleTypeVar, TypeQLVariable.Concept overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Plays(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeStatement relates(String roleType) {
        return constrain(new TypeConstraint.Relates(roleType));
    }

    default TypeStatement relates(TypeQLVariable.Concept roleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleTypeVar));
    }

    default TypeStatement relates(String roleType, String overriddenRoleType) {
        return constrain(new TypeConstraint.Relates(roleType, overriddenRoleType));
    }

    default TypeStatement relates(String roleType, TypeQLVariable.Concept overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleType, overriddenRoleTypeVar));
    }

    default TypeStatement relates(TypeQLVariable.Concept roleTypeVar, String overriddenRoleType) {
        return constrain(new TypeConstraint.Relates(roleTypeVar, overriddenRoleType));
    }

    default TypeStatement relates(TypeQLVariable.Concept roleTypeVar, TypeQLVariable.Concept overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeStatement value(TypeQLArg.ValueType ValueType) {
        return constrain(new TypeConstraint.ValueType(ValueType));
    }

    default TypeStatement regex(String regex) {
        return constrain(new TypeConstraint.Regex(regex));
    }

    TypeStatement constrain(TypeConstraint.Label constraint);

    TypeStatement constrain(TypeConstraint.Sub constraint);

    TypeStatement constrain(TypeConstraint.Abstract constraint);

    TypeStatement constrain(TypeConstraint.ValueType constraint);

    TypeStatement constrain(TypeConstraint.Regex constraint);

    TypeStatement constrain(TypeConstraint.Owns constraint);

    TypeStatement constrain(TypeConstraint.Plays constraint);

    TypeStatement constrain(TypeConstraint.Relates constraint);
}
