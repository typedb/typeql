/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.constraint;

import com.typeql.lang.common.TypeQLToken;
import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.pattern.expression.Expression;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class ValueConstraint extends Constraint {

    @Override
    public boolean isValue() {
        return true;
    }

    @Override
    public ValueConstraint asValue() {
        return this;
    }

    public boolean isPredicate() {
        return false;
    }

    public boolean isAssignment() {
        return false;
    }

    public Predicate asPredicate() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.class)));
    }

    public Assignment asAssignment() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Assignment.class)));
    }

    public static class Predicate extends ValueConstraint {

        private final com.typeql.lang.pattern.constraint.Predicate<?> predicate;
        private final Set<TypeQLVariable> variables;
        private final int hash;

        public Predicate(com.typeql.lang.pattern.constraint.Predicate<?> predicate) {
            this.predicate = predicate;
            this.variables = predicate.variables().stream().map(TypeQLVariable::cloneVar).collect(Collectors.toSet());
            this.hash = Objects.hash(Predicate.class, this.predicate);
        }

        @Override
        public Set<TypeQLVariable> variables() {
            return variables;
        }

        public com.typeql.lang.pattern.constraint.Predicate<?> predicate() {
            return predicate;
        }

        @Override
        public boolean isPredicate() {
            return true;
        }

        @Override
        public Predicate asPredicate() {
            return this;
        }

        @Override
        public java.lang.String toString() {
            return predicate.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Predicate that = (Predicate) o;
            return this.predicate.equals(that.predicate);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Assignment extends ValueConstraint {
        private final Expression expression;
        private final Set<TypeQLVariable> inputs;
        private final int hash;

        public Assignment(Expression expression) {
            this.expression = expression;
            this.inputs = expression.variables().stream().collect(Collectors.toSet());
            this.hash = Objects.hash(Assignment.class, this.expression);
        }

        public Expression expression() {
            return expression;
        }

        @Override
        public Set<TypeQLVariable> variables() {
            return inputs;
        }

        public boolean isAssignment() {
            return true;
        }

        public Assignment asAssignment() {
            return this;
        }

        @Override
        public String toString() {
            return TypeQLToken.Constraint.ASSIGN.toString() + SPACE + this.expression.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Assignment that = (Assignment) o;
            return this.expression.equals(that.expression);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
