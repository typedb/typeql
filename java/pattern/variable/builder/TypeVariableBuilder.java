/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.variable.TypeVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundConceptVariable;

public interface TypeVariableBuilder {

    default TypeVariable type(TypeQLToken.Type type) {
        return type(type.toString());
    }

    default TypeVariable type(String label) {
        return constrain(new TypeConstraint.Label(label));
    }

    default TypeVariable type(String scope, String label) {
        return constrain(new TypeConstraint.Label(scope, label));
    }

    default TypeVariable isAbstract() {
        return constrain(new TypeConstraint.Abstract());
    }

    default TypeVariable sub(TypeQLToken.Type type) {
        return sub(type.toString());
    }

    default TypeVariable sub(String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeLabel, false));
    }

    default TypeVariable sub(String typeScope, String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeScope, typeLabel, false));
    }

    default TypeVariable sub(UnboundConceptVariable typeVar) {
        return constrain(new TypeConstraint.Sub(typeVar, false));
    }

    default TypeVariable subX(TypeQLToken.Type type) {
        return subX(type.toString());
    }

    default TypeVariable subX(String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeLabel, true));
    }

    default TypeVariable subX(String typeScope, String typeLabel) {
        return constrain(new TypeConstraint.Sub(typeScope, typeLabel, true));
    }

    default TypeVariable subX(UnboundConceptVariable typeVar) {
        return constrain(new TypeConstraint.Sub(typeVar, true));
    }

    default TypeVariable owns(String attributeType, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeType, annotations));
    }

    default TypeVariable owns(UnboundConceptVariable attributeTypeVar, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, annotations));
    }


    default TypeVariable owns(String attributeType, String overriddenAttributeType, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeType, annotations));
    }

    default TypeVariable owns(String attributeType, UnboundConceptVariable overriddenAttributeTypeVar, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeType, overriddenAttributeTypeVar, annotations));
    }

    default TypeVariable owns(UnboundConceptVariable attributeTypeVar, String overriddenAttributeType, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeType, annotations));
    }

    default TypeVariable owns(UnboundConceptVariable attributeTypeVar, UnboundConceptVariable overriddenAttributeTypeVar, TypeQLToken.Annotation... annotations) {
        return constrain(new TypeConstraint.Owns(attributeTypeVar, overriddenAttributeTypeVar, annotations));
    }

    default TypeVariable plays(String relationType, String roleType) {
        return constrain(new TypeConstraint.Plays(relationType, roleType));
    }

    default TypeVariable plays(UnboundConceptVariable roleTypevar) {
        return constrain(new TypeConstraint.Plays(roleTypevar));
    }

    default TypeVariable plays(String relationType, String roleType, String overriddenRoleType) {
        return constrain(new TypeConstraint.Plays(relationType, roleType, overriddenRoleType));
    }

    default TypeVariable plays(String relationType, String roleType, UnboundConceptVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Plays(relationType, roleType, overriddenRoleTypeVar));
    }

    default TypeVariable plays(UnboundConceptVariable roleTypeVar, String overriddenRoleType) {
        return constrain(new TypeConstraint.Plays(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable plays(UnboundConceptVariable roleTypeVar, UnboundConceptVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Plays(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable relates(String roleType) {
        return constrain(new TypeConstraint.Relates(roleType));
    }

    default TypeVariable relates(UnboundConceptVariable roleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleTypeVar));
    }

    default TypeVariable relates(String roleType, String overriddenRoleType) {
        return constrain(new TypeConstraint.Relates(roleType, overriddenRoleType));
    }

    default TypeVariable relates(String roleType, UnboundConceptVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleType, overriddenRoleTypeVar));
    }

    default TypeVariable relates(UnboundConceptVariable roleTypeVar, String overriddenRoleType) {
        return constrain(new TypeConstraint.Relates(roleTypeVar, overriddenRoleType));
    }

    default TypeVariable relates(UnboundConceptVariable roleTypeVar, UnboundConceptVariable overriddenRoleTypeVar) {
        return constrain(new TypeConstraint.Relates(roleTypeVar, overriddenRoleTypeVar));
    }

    default TypeVariable value(TypeQLArg.ValueType ValueType) {
        return constrain(new TypeConstraint.ValueType(ValueType));
    }

    default TypeVariable regex(String regex) {
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
