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

package com.vaticle.typeql.lang.pattern.statement;

import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;

public class ValueStatement extends Statement {
    private final TypeQLVariable.Value variable;
    private ValueConstraint.Assignment assignmentConstraint;
    private ValueConstraint.Predicate predicateConstraint;
    private final List<ValueConstraint> constraints;

    private ValueStatement(TypeQLVariable.Value variable) {
        this.variable = variable;
        constraints = new ArrayList<>();
    }

    public static ValueStatement of(TypeQLVariable.Value variable) {
        return new ValueStatement(variable);
    }

    @Override
    public TypeQLVariable.Value headVariable() {
        return variable;
    }

    public ValueStatement constrain(ValueConstraint.Assignment assignmentConstraint) {
        if (this.assignmentConstraint != null || this.predicateConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, ValueConstraint.class, assignmentConstraint));
        }
        this.assignmentConstraint = assignmentConstraint;
        this.constraints.add(assignmentConstraint);
        return this;
    }

    public ValueStatement constrain(ValueConstraint.Predicate predicateConstraint) {
        if (this.assignmentConstraint != null || this.predicateConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, ValueConstraint.class, predicateConstraint));
        }
        this.predicateConstraint = predicateConstraint;
        this.constraints.add(predicateConstraint);
        return this;
    }

    @Override
    public List<ValueConstraint> constraints() {
        return constraints;
    }

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public ValueStatement asValue() {
        return this;
    }

    @Override
    public String toString(boolean pretty) {
        assert constraints.size() <= 1;
        return variable + ((constraints.isEmpty()) ? "" : (SPACE + constraints.get(0).toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueStatement that = (ValueStatement) o;
        return this.variable.equals(that.variable) && this.constraints.equals(that.constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, constraints);
    }
}
