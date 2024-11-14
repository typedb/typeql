/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.constraint;

import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.pattern.statement.ConceptStatement;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.typedb.common.collection.Collections.set;
import static com.typedb.common.util.Objects.className;
import static com.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.typeql.lang.common.TypeQLToken.Constraint.IS;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static java.util.Collections.singleton;

public abstract class ConceptConstraint extends Constraint {

    @Override
    public boolean isConcept() {
        return true;
    }

    @Override
    public ConceptConstraint asConcept() {
        return this;
    }

    public boolean isIs() {
        return false;
    }

    public ConceptConstraint.Is asIs() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ConceptConstraint.Is.class)));
    }

    public static class Is extends ConceptConstraint {

        private final TypeQLVariable.Concept variable;
        private final int hash;

        public Is(TypeQLVariable.Concept variable) {
            if (variable == null) throw new NullPointerException("Null var");
            this.variable = variable;
            this.hash = Objects.hash(Is.class, this.variable);
        }

        public TypeQLVariable.Concept variable() {
            return variable;
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            return singleton(variable());
        }

        @Override
        public boolean isIs() {
            return true;
        }

        @Override
        public ConceptConstraint.Is asIs() {
            return this;
        }

        @Override
        public String toString() {
            return IS.toString() + SPACE + variable();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Is that = (Is) o;
            return (this.variable.equals(that.variable));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
