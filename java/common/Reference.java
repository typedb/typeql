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

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_VARIABLE_NAME;

public abstract class Reference {

    final Type type;
    final boolean isVisible;

    enum Type {NAME_CONCEPT, NAME_VALUE, ANONYMOUS, LABEL,}

    Reference(Type type, boolean isVisible) {
        this.type = type;
        this.isVisible = isVisible;
    }

    public static Name.Concept concept(String name) {
        return new Name.Concept(name);
    }

    public static Name.Value value(String name) {
        return new Name.Value(name);
    }

    public static Reference.Label label(String label) {
        return new Label(label, null);
    }

    public static Reference.Label label(String label, String scope) {
        return new Label(label, scope);
    }

    public static Reference.Anonymous anonymous(boolean isVisible) {
        return new Reference.Anonymous(isVisible);
    }

    protected Reference.Type type() {
        return type;
    }

    public abstract String name();

    public String syntax() {
        if (type == Type.LABEL) return asLabel().scopedLabel();
        else return (type == Type.NAME_VALUE ? TypeQLToken.Char.QUESTION_MARK : TypeQLToken.Char.$) + name();
    }

    protected boolean isVisible() {
        return isVisible;
    }

    public boolean isName() {
        return false;
    }

    public boolean isNameConcept() {
        return type == Type.NAME_CONCEPT;
    }

    public boolean isNameValue() {
        return type == Type.NAME_VALUE;
    }

    public boolean isLabel() {
        return type == Type.LABEL;
    }

    public boolean isAnonymous() {
        return type == Type.ANONYMOUS;
    }

    public Name asName() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Name.class)));
    }

    public Reference.Label asLabel() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Label.class)));
    }

    public Reference.Anonymous asAnonymous() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Anonymous.class)));
    }

    @Override
    public String toString() {
        return syntax();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public static abstract class Name extends Reference {

        public static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
        final String name;
        private final int hash;

        Name(Type type, String name, boolean isVisible) {
            super(type, isVisible);
            if (!REGEX.matcher(name).matches())
                throw TypeQLException.of(INVALID_VARIABLE_NAME.message(name, REGEX.pattern()));
            this.name = name;
            this.hash = Objects.hash(this.type, this.isVisible, this.name);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean isName() {
            return true;
        }

        @Override
        public Name asName() {
            return this;
        }

        public Concept asConcept() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Concept.class)));
        }

        public Value asValue() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.class)));
        }

        @Override
        public int hashCode() {
            return hash;
        }


        public static class Value extends Name {

            private Value(String name) {
                super(Type.NAME_VALUE, name, true);
            }

            @Override
            public Value asValue() {
                return this;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Value that = (Value) o;
                return (this.type == that.type && this.isVisible == that.isVisible && this.name.equals(that.name));
            }
        }

        public static class Concept extends Name {

            private Concept(String name) {
                super(Type.NAME_CONCEPT, name, true);
            }

            @Override
            public Concept asConcept() {
                return this;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Concept that = (Concept) o;
                return (this.type == that.type && this.isVisible == that.isVisible && this.name.equals(that.name));
            }
        }
    }

    public static class Label extends Reference {

        private final String label;
        private final String scope;

        private final int hash;

        Label(String label, @Nullable String scope) {
            super(Type.LABEL, false);
            this.label = label;
            this.scope = scope;
            this.hash = Objects.hash(this.type, this.isVisible, this.label, this.scope);
        }

        public String label() {
            return label;
        }

        public Optional<String> scope() {
            return Optional.ofNullable(scope);
        }

        public String scopedLabel() {
            if (scope == null) return label;
            else return scope + COLON + label;
        }

        @Override
        public String name() {
            return scopedLabel();
        }

        @Override
        public Reference.Label asLabel() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label that = (Label) o;
            return (this.type == that.type &&
                    this.isVisible == that.isVisible &&
                    this.label.equals(that.label) &&
                    Objects.equals(this.scope, that.scope));

        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Anonymous extends Reference {

        private final int hash;

        private Anonymous(boolean isVisible) {
            super(Type.ANONYMOUS, isVisible);
            this.hash = Objects.hash(this.type, this.isVisible);
        }

        @Override
        public String name() {
            return TypeQLToken.Char.UNDERSCORE.toString();
        }

        @Override
        public Reference.Anonymous asAnonymous() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Anonymous that = (Anonymous) o;
            return (this.type == that.type && this.isVisible == that.isVisible);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }


}
