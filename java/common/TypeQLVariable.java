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

package com.vaticle.typeql.lang.common;

import com.vaticle.typeql.lang.common.exception.TypeQLException;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class TypeQLVariable {

    protected final Reference reference;

    protected TypeQLVariable(Reference reference) {
        this.reference = reference;
    }

    public boolean isConcept() {
        return false;
    }

    public Concept asConcept() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Concept.class)));
    }

    public boolean isValue() {
        return false;
    }

    public Value asValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.class)));
    }

    public Reference.Type type() {
        return reference.type();
    }

    public String name() {
        switch (reference.type()) {
            case NAME_CONCEPT:
            case NAME_VALUE:
                return reference.name();
            case LABEL:
            case ANONYMOUS:
                return null;
            default:
                assert false;
                return null;
        }
    }

    public Reference reference() {
        return reference;
    }

    public boolean isNamed() {
        return reference.isName();
    }

    public boolean isNamedConcept() {
        return reference.isNameConcept();
    }

    public boolean isNamedValue() {
        return reference.isNameValue();
    }

    public boolean isLabelled() {
        return reference.isLabel();
    }

    public boolean isAnonymised() {
        return reference.isAnonymous();
    }

    public boolean isVisible() {
        return reference.isVisible();
    }

    public abstract TypeQLVariable cloneVar();

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean pretty) {
        return reference.syntax();
    }

    /**
     * Note that this equality function relies on the reference to make sure that a named Value variable
     * and a named Concept variable are equal.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeQLVariable)) return false;
        TypeQLVariable that = (TypeQLVariable) o;
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }

    public static class Concept extends TypeQLVariable {

        protected Concept(Reference reference) {
            super(reference);
            assert !reference.isNameValue();
        }

        public static Concept nameVar(String name) {
            return new Concept(Reference.Name.concept(name));
        }

        public static Concept labelVar(String label) {
            return new Concept(Reference.label(label));
        }

        public static Concept labelVar(String label, String scope) {
            return new Concept(Reference.label(label, scope));
        }

        public static Concept anonymousVar() {
            return new Concept(Reference.anonymous(true));
        }

        public static Concept hiddenVar() {
            return new Concept(Reference.anonymous(false));
        }

        @Override
        public boolean isConcept() {
            return true;
        }

        @Override
        public Concept asConcept() {
            return this;
        }

        @Override
        public final TypeQLVariable.Concept cloneVar() {
            return new Concept(reference);
        }
    }

    public static class Value extends TypeQLVariable {

        protected Value(Reference.Name.Value reference) {
            super(reference);
        }

        public static Value nameVar(String name) {
            return new Value(Reference.Name.value(name));
        }

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public Value asValue() {
            return this;
        }

        @Override
        public final TypeQLVariable.Value cloneVar() {
            return new Value(reference.asName().asValue());
        }
    }
}
