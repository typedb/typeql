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
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.variable.builder.ConceptVariableBuilder;
import com.vaticle.typeql.lang.pattern.variable.builder.Expression;
import com.vaticle.typeql.lang.pattern.variable.builder.ThingVariableBuilder;
import com.vaticle.typeql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.Set;

public class UnboundConceptVariable extends UnboundVariable implements
        ConceptVariableBuilder,
        TypeVariableBuilder,
        ThingVariableBuilder.Common<ThingVariable.Thing>,
        ThingVariableBuilder.Thing,
        ThingVariableBuilder.Relation,
        ThingVariableBuilder.Attribute,
        Expression {

    UnboundConceptVariable(Reference reference) {
        super(reference);
        assert !reference.isNameValue();
    }

    public static UnboundConceptVariable named(String name) {
        return new UnboundConceptVariable(Reference.concept(name));
    }

    public static UnboundConceptVariable anonymous() {
        return new UnboundConceptVariable(Reference.anonymous(true));
    }

    public static UnboundConceptVariable hidden() {
        return new UnboundConceptVariable(Reference.anonymous(false));
    }

    @Override
    public boolean isConceptVariable() {
        return true;
    }

    @Override
    public UnboundConceptVariable asConceptVariable() {
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
    public void collectVariables(Set<UnboundVariable> collector) {
        collector.add(this);
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
    public ThingVariable.Attribute constrain(Predicate<?> predicate) {
        return constrain(new ThingConstraint.Predicate(predicate));
    }

    @Override
    public ThingVariable.Attribute constrain(ThingConstraint.Predicate constraint) {
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
    public String toString(boolean pretty) {
        return reference.syntax();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnboundConceptVariable that = (UnboundConceptVariable) o;
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
