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

package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.pattern.constraint.ConceptConstraint;
import com.vaticle.typeql.lang.pattern.constraint.Constraint;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.variable.builder.ConceptVariableBuilder;
import com.vaticle.typeql.lang.pattern.variable.builder.ThingVariableBuilder;
import com.vaticle.typeql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.Collections;
import java.util.List;

public class UnboundVariable extends Variable implements ConceptVariableBuilder,
                                                         TypeVariableBuilder,
                                                         ThingVariableBuilder.Common<ThingVariable.Thing>,
                                                         ThingVariableBuilder.Thing,
                                                         ThingVariableBuilder.Relation,
                                                         ThingVariableBuilder.Attribute {

    UnboundVariable(Reference reference) {
        super(reference);
    }

    public static UnboundVariable named(String name) {
        return new UnboundVariable(Reference.name(name));
    }

    public static UnboundVariable anonymous() {
        return new UnboundVariable(Reference.anonymous(true));
    }

    public static UnboundVariable hidden() {
        return new UnboundVariable(Reference.anonymous(false));
    }

    @Override
    public boolean isUnbound() {
        return true;
    }

    @Override
    public UnboundVariable asUnbound() {
        return this;
    }

    public ConceptVariable toConcept() {
        return new ConceptVariable(reference);
    }

    public TypeVariable toType() {
        return new TypeVariable(reference);
    }

    public ThingVariable<?> toThing() {
        return new ThingVariable.Thing(reference);
    }

    @Override
    public List<Constraint<?>> constraints() {
        return Collections.emptyList();
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Label constraint) {
        Reference ref = reference;
        if (reference.isAnonymous()) ref = Reference.label(constraint.scopedLabel());
        return new TypeVariable(ref).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Sub constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Abstract constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.ValueType constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Regex constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Owns constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Plays constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Relates constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public ThingVariable.Thing constrain(ThingConstraint.Isa constraint) {
        return new ThingVariable.Thing(reference).constrain(constraint);
    }

    @Override
    public ThingVariable.Thing constrain(ThingConstraint.Has constraint) {
        return new ThingVariable.Thing(reference).constrain(constraint);
    }

    @Override
    public ThingVariable.Thing constrain(ThingConstraint.IID constraint) {
        return new ThingVariable.Thing(reference, constraint);
    }

    @Override
    public ConceptVariable constrain(ConceptConstraint.Is constraint) {
        return new ConceptVariable(reference, constraint);
    }

    @Override
    public ThingVariable.Attribute constrain(ThingConstraint.Value<?> constraint) {
        return new ThingVariable.Attribute(reference, constraint);
    }

    @Override
    public ThingVariable.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer) {
        return constrain(new ThingConstraint.Relation(rolePlayer));
    }

    public ThingVariable.Relation constrain(ThingConstraint.Relation constraint) {
        return new ThingVariable.Relation(reference, constraint);
    }

    @Override
    public String toString() {
        return reference.syntax();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnboundVariable that = (UnboundVariable) o;
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
