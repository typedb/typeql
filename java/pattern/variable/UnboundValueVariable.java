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

import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.variable.builder.Expression;
import com.vaticle.typeql.lang.pattern.variable.builder.PredicateBuilder;
import com.vaticle.typeql.lang.pattern.variable.builder.ValueVariableBuilder;

import java.util.Set;

public class UnboundValueVariable extends UnboundVariable implements
        ValueVariableBuilder,
        Expression {

    UnboundValueVariable(Reference.Name.Value reference) {
        super(reference);
    }

    public static UnboundValueVariable named(String name) {
        return new UnboundValueVariable(Reference.value(name));
    }

    @Override
    public boolean isValueVariable() {
        return true;
    }

    @Override
    public UnboundValueVariable asValueVariable() {
        return this;
    }

    public ValueVariable toBound() {
        return new ValueVariable(reference.asName().asValue());
    }

    public ValueVariable constrain(ValueConstraint.Assignment constraint) {
        return new ValueVariable(reference.asName().asValue()).constrain(constraint);
    }

    public ValueVariable constrain(Predicate<?> predicate) {
        return constrain(new ValueConstraint.Predicate(predicate));
    }

    public ValueVariable constrain(ValueConstraint.Predicate constraint) {
        return new ValueVariable(reference.asName().asValue()).constrain(constraint);
    }

    @Override
    public void collectVariables(Set<UnboundVariable> collector) {
        collector.add(this);
    }

    @Override
    public String toString(boolean pretty) {
        return reference.syntax();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnboundValueVariable that = (UnboundValueVariable) o;
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
