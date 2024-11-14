/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.common;

import com.typeql.lang.common.exception.TypeQLException;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.typedb.common.util.Objects.className;
import static com.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_TYPE_LABEL;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_VARIABLE_NAME;

public abstract class Reference {

    private static final String IDENTIFIER_CHAR = "A-Za-z" +
            "\\u00C0-\\u00D6" +
            "\\u00D8-\\u00F6" +
            "\\u00F8-\\u02FF" +
            "\\u0370-\\u037D" +
            "\\u037F-\\u1FFF" +
            "\\u200C-\\u200D" +
            "\\u2070-\\u218F" +
            "\\u2C00-\\u2FEF" +
            "\\u3001-\\uD7FF" +
            "\\uF900-\\uFDCF" +
            "\\uFDF0-\\uFFFD";
    private static final String IDENTIFIER_DIGIT = "0-9";
    private static final String IDENTIFIER_CONNECTOR = "_" +
            "\\-" +
            "\\u00B7" +
            "\\u0300-\\u036F" +
            "\\u203F-\\u2040";

    public static final Pattern IDENTIFIER_VAR = Pattern.compile(
            "^[" + IDENTIFIER_CHAR + IDENTIFIER_DIGIT + "][" + IDENTIFIER_CHAR + IDENTIFIER_DIGIT + IDENTIFIER_CONNECTOR + "]*$"
    );
    public static final Pattern IDENTIFIER_LABEL = Pattern.compile(
            "^[" + IDENTIFIER_CHAR  + "][" + IDENTIFIER_CHAR + IDENTIFIER_DIGIT + IDENTIFIER_CONNECTOR + "]*$"
    );

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

        final String name;
        private final int hash;

        Name(Type type, String name, boolean isVisible) {
            super(type, isVisible);
            if (!IDENTIFIER_VAR.matcher(name).matches()) {
                throw TypeQLException.of(INVALID_VARIABLE_NAME.message(name));
            }
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
            if (!IDENTIFIER_LABEL.matcher(label).matches()) {
                throw TypeQLException.of(INVALID_TYPE_LABEL.message(label));
            } else if (scope != null && !IDENTIFIER_LABEL.matcher(scope).matches()) {
                throw TypeQLException.of(INVALID_TYPE_LABEL.message(scope));
            }
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
