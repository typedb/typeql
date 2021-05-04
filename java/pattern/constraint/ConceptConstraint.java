/*
 * Copyright (C) 2021 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.ConceptVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Constraint.IS;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class ConceptConstraint extends Constraint<ConceptVariable> {

    @Override
    public boolean isConcept() {
        return true;
    }

    @Override
    public ConceptConstraint asConcept() {
        return this;
    }

    @Override
    public Set<ConceptVariable> variables() {
        return null;
    }

    public boolean isIs() {
        return false;
    }

    public ConceptConstraint.Is asIs() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ConceptConstraint.Is.class)));
    }

    public static class Is extends ConceptConstraint {

        private final ConceptVariable variable;
        private final int hash;

        public Is(UnboundVariable variable) {
            this(variable.toConcept());
        }

        private Is(ConceptVariable variable) {
            if (variable == null) throw new NullPointerException("Null var");
            this.variable = variable;
            this.hash = Objects.hash(Is.class, this.variable);
        }

        public ConceptVariable variable() {
            return variable;
        }

        @Override
        public Set<ConceptVariable> variables() {
            return set(variable());
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
