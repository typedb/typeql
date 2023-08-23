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
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.expression.Expression;
import com.vaticle.typeql.lang.pattern.statement.ValueStatement;
import com.vaticle.typeql.lang.pattern.statement.builder.ValueStatementBuilder;
import com.vaticle.typeql.lang.query.TypeQLFetch;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;

public class ValueVariableBuilder extends TypeQLVariable.Value implements
        ValueStatementBuilder,
        Expression,
        TypeQLFetch.Key.Var.UnlabelledVar {

    ValueVariableBuilder(Reference.Name.Value reference) {
        super(reference);
    }

    public static ValueVariableBuilder named(String name) {
        return new ValueVariableBuilder(Reference.value(name));
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public ValueVariableBuilder asValue() {
        return this;
    }

    public ValueStatement toStatement() {
        return ValueStatement.of(this);
    }

    public ValueStatement constrain(Predicate<?> predicate) {
        return constrain(new ValueConstraint.Predicate(predicate));
    }

    public ValueStatement constrain(ValueConstraint.Predicate constraint) {
        return toStatement().constrain(constraint);
    }

    public ValueStatement constrain(ValueConstraint.Assignment constraint) {
        return toStatement().constrain(constraint);
    }

    public void collectVariables(Set<TypeQLVariable> collector) {
        collector.add(this);
    }

    @Override
    public LabelledVar asLabel(TypeQLFetch.Key.Label label) {
        return new LabelledVar(this, label);
    }

    @Override
    public Attribute projectAttr(Pair<Reference.Label, TypeQLFetch.Key.Label> attribute) {
        return new Attribute(this, list(attribute));
    }

    @Override
    public Attribute projectAttrs(Stream<Pair<Reference.Label, TypeQLFetch.Key.Label>> attributes) {
        return new Attribute(this, attributes.collect(Collectors.toList()));
    }
}
