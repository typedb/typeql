/*
 * Copyright (C) 2021 Grakn Labs
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

package graql.lang.pattern.variable;

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;

import java.util.Objects;
import java.util.regex.Pattern;

import static grakn.common.util.Objects.className;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static graql.lang.common.exception.ErrorMessage.INVALID_VARIABLE_NAME;

public abstract class Reference {

    final Type type;
    final boolean isVisible;

    public enum Type {NAME, LABEL, ANONYMOUS, SYSTEM}

    Reference(Type type, boolean isVisible) {
        this.type = type;
        this.isVisible = isVisible;
    }

    public static Reference.Name named(String name) {
        return new Name(name);
    }

    public static Reference.Label label(String label) {
        return new Label(label);
    }

    public static Reference.Anonymous anonymous(boolean isVisible) {
        return new Reference.Anonymous(isVisible);
    }

    protected Reference.Type type() {
        return type;
    }

    public abstract String syntax();

    protected boolean isVisible() {
        return isVisible;
    }

    public boolean isReferrable() {
        return !isAnonymous();
    }

    public boolean isName() {
        return type == Type.NAME;
    }

    public boolean isLabel() {
        return type == Type.LABEL;
    }

    public boolean isAnonymous() {
        return type == Type.ANONYMOUS;
    }

    public Reference.Referrable asReferrable() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Referrable.class)));
    }

    public Reference.Name asName() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Name.class)));
    }

    public Reference.Label asLabel() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Label.class)));
    }

    public Reference.Anonymous asAnonymous() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Anonymous.class)));
    }

    @Override
    public String toString() {
        return syntax();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public static abstract class Referrable extends Reference {

        public Referrable(Type type, boolean isVisible) {
            super(type, isVisible);
        }

        @Override
        public Reference.Referrable asReferrable() {
            return this;
        }
    }

    public static class Name extends Referrable {

        private static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
        protected final String name;
        private final int hash;

        protected Name(String name) {
            super(Type.NAME, true);
            if (!REGEX.matcher(name).matches()) {
                throw GraqlException.of(INVALID_VARIABLE_NAME.message(name, REGEX.toString()));
            }
            this.name = name;
            this.hash = Objects.hash(this.type, this.isVisible, this.name);
        }

        public String name() {
            return name;
        }

        @Override
        public String syntax() {
            return GraqlToken.Char.$ + name;
        }

        @Override
        public Name asName() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Name that = (Name) o;
            return (this.type == that.type &&
                    this.isVisible == that.isVisible &&
                    this.name.equals(that.name));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Label extends Referrable {

        private final String label;
        private final int hash;

        Label(String label) {
            super(Type.LABEL, false);
            this.label = label;
            this.hash = Objects.hash(this.type, this.isVisible, this.label);
        }

        String label() {
            return label;
        }

        @Override
        public String syntax() {
            return GraqlToken.Char.$_ + label;
        }

        @Override
        public Reference.Label asLabel() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Label that = (Label) o;
            return (this.type == that.type &&
                    this.isVisible == that.isVisible &&
                    this.label.equals(that.label));
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
        public String syntax() {
            return GraqlToken.Char.$_.toString();
        }

        @Override
        public Reference.Anonymous asAnonymous() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Anonymous that = (Anonymous) o;
            return (this.type == that.type && this.isVisible == that.isVisible);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
