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

package com.vaticle.typeql.lang.builder;

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.Reference;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.pattern.constraint.ConceptConstraint;
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.expression.Expression;
import com.vaticle.typeql.lang.pattern.statement.ConceptStatement;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;
import com.vaticle.typeql.lang.pattern.statement.TypeStatement;
import com.vaticle.typeql.lang.pattern.statement.builder.ConceptStatementBuilder;
import com.vaticle.typeql.lang.pattern.statement.builder.ThingStatementBuilder;
import com.vaticle.typeql.lang.pattern.statement.builder.TypeStatementBuilder;
import com.vaticle.typeql.lang.query.TypeQLFetch;

import java.util.List;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;

public class ConceptVariableBuilder extends TypeQLVariable.Concept implements
        ConceptStatementBuilder,
        TypeStatementBuilder,
        ThingStatementBuilder.Common<ThingStatement.Thing>,
        ThingStatementBuilder.Thing,
        ThingStatementBuilder.Relation,
        ThingStatementBuilder.Attribute,
        Expression,
        TypeQLFetch.Key.Var.UnlabelledVar {

    private ConceptVariableBuilder(Reference reference) {
        super(reference);
    }

    public static ConceptVariableBuilder named(String name) {
        return new ConceptVariableBuilder(Reference.concept(name));
    }

    public static ConceptVariableBuilder anonymous() {
        return new ConceptVariableBuilder(Reference.anonymous(true));
    }

    public static ConceptVariableBuilder hidden() {
        return new ConceptVariableBuilder(Reference.anonymous(false));
    }

    public static TypeStatement label(String label) {
        return new ConceptVariableBuilder(Reference.label(label)).toTypeStatement();
    }

    public static TypeStatement label(String label, String scope) {
        return new ConceptVariableBuilder(Reference.label(label, scope)).toTypeStatement();
    }

    @Override
    public boolean isConceptVar() {
        return true;
    }

    @Override
    public ConceptVariableBuilder asConceptVar() {
        return this;
    }

    public ConceptStatement toConceptStatement() {
        return ConceptStatement.of(this);
    }

    public TypeStatement toTypeStatement() {
        return TypeStatement.of(this);
    }

    public ThingStatement.Thing toThingStatement() {
        return ThingStatement.Thing.of(this);
    }

    @Override
    public void collectVariables(Set<TypeQLVariable> collector) {
        collector.add(this);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Label constraint) {
        TypeQLVariable.Concept variable;
        if (reference.isAnonymous())
            variable = TypeQLVariable.Concept.labelVar(constraint.label(), constraint.scope().orElse(null));
        else variable = this;
        return TypeStatement.of(variable).constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Sub constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Abstract constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.ValueType constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Regex constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Owns constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Plays constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Relates constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public ThingStatement.Thing constrain(ThingConstraint.Isa constraint) {
        return ThingStatement.Thing.of(this).constrain(constraint);
    }

    @Override
    public ThingStatement.Thing constrain(ThingConstraint.Has constraint) {
        return ThingStatement.Thing.of(this).constrain(constraint);
    }

    @Override
    public ThingStatement.Thing constrain(ThingConstraint.IID constraint) {
        return ThingStatement.Thing.of(this, constraint);
    }

    @Override
    public ConceptStatement constrain(ConceptConstraint.Is constraint) {
        return ConceptStatement.of(this, constraint);
    }

    @Override
    public ThingStatement.Attribute constrain(Predicate<?> predicate) {
        return constrain(new ThingConstraint.Predicate(predicate));
    }

    @Override
    public ThingStatement.Attribute constrain(ThingConstraint.Predicate constraint) {
        return ThingStatement.Attribute.of(this, constraint);
    }

    @Override
    public ThingStatement.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer) {
        return constrain(new ThingConstraint.Relation(rolePlayer));
    }

    public ThingStatement.Relation constrain(ThingConstraint.Relation constraint) {
        return ThingStatement.Relation.of(this, constraint);
    }

    @Override
    public LabelledVar asLabel(TypeQLFetch.Key.Label label) {
        return new LabelledVar(this, label);
    }

    @Override
    public Attribute fetch(Pair<Reference.Label, TypeQLFetch.Key.Label> attribute) {
        return new Attribute(this, list(attribute));
    }

    @Override
    public Attribute fetch(List<Pair<Reference.Label, Label>> attributes) {
        return new Attribute(this, attributes);
    }
}
