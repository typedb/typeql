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
