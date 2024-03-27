/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
