/*
 * Copyright (C) 2020 Grakn Labs
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

import static graql.lang.common.exception.ErrorMessage.INVALID_CAST_EXCEPTION;
import static graql.lang.common.exception.ErrorMessage.INVALID_VARIABLE_NAME;

public abstract class Identity {

    final Type type;
    final boolean isVisible;

    enum Type {NAME, LABEL, ANONYMOUS}

    Identity(Type type, boolean isVisible) {
        this.type = type;
        this.isVisible = isVisible;
    }

    static Identity.Name named(String name) {
        return new Name(name);
    }

    static Identity.Label label(String label) {
        return new Label(label);
    }

    static Identity.Anonymous anonymous(boolean isVisible) {
        return new Identity.Anonymous(isVisible);
    }

    static Identity.AnonymousWithID anonymous(boolean isVisible, int id) {
        return new Identity.AnonymousWithID(isVisible, id);
    }

    Identity.Type type() {
        return type;
    }

    abstract String syntax();

    abstract String identifier();

    boolean isVisible() {
        return isVisible;
    }

    Identity.Name asNamed() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                this.getClass().getCanonicalName(), Name.class.getCanonicalName()
        ));
    }

    Identity.Label asLabel() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                this.getClass().getCanonicalName(), Label.class.getCanonicalName()
        ));
    }

    Identity.Anonymous asAnonymous() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                this.getClass().getCanonicalName(), Anonymous.class.getCanonicalName()
        ));
    }

    @Override
    public String toString() {
        return syntax();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    static class Name extends Identity {

        private static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
        protected final String name;
        private final int hash;

        Name(String name) {
            this(name, true);
        }

        private Name(String name, boolean isVisible) {
            super(Type.NAME, isVisible);
            if (!REGEX.matcher(name).matches())
                throw GraqlException.create(INVALID_VARIABLE_NAME.message(name, REGEX.toString()));
            this.name = name;
            this.hash = Objects.hash(this.type, this.isVisible, this.name);
        }

        String name() {
            return name;
        }

        @Override
        String syntax() {
            return GraqlToken.Char.$ + name;
        }

        @Override
        String identifier() {
            return syntax();
        }

        @Override
        Name asNamed() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Name that = (Name) o;
            return (this.type == that.type &&
                    this.isVisible == that.isVisible &&
                    this.name.equals(that.name));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Label extends Identity {

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
        String syntax() {
            return GraqlToken.Char.$_ + label;
        }

        @Override
        String identifier() {
            return syntax();
        }

        @Override
        Identity.Label asLabel() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label that = (Label) o;
            return (this.type == that.type &&
                    this.isVisible == that.isVisible &&
                    this.label.equals(that.label));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    static class Anonymous extends Identity {

        private final int hash;

        private Anonymous(boolean isVisible) {
            super(Type.ANONYMOUS, isVisible);
            this.hash = Objects.hash(this.type, this.isVisible);
        }

        @Override
        String syntax() {
            return GraqlToken.Char.$_.toString();
        }

        @Override
        String identifier() {
            return syntax();
        }

        @Override
        Identity.Anonymous asAnonymous() {
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

    static class AnonymousWithID extends Identity.Anonymous {

        private final int id;
        private final int hash;

        private AnonymousWithID(boolean isVisible, int id) {
            super(isVisible);
            this.id = id;
            this.hash = Objects.hash(this.type, this.isVisible, this.id);
        }

        @Override
        String identifier() {
            return syntax() + id;
        }

        @Override
        Identity.AnonymousWithID asAnonymous() {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AnonymousWithID that = (AnonymousWithID) o;
            return (this.type == that.type && this.isVisible == that.isVisible && this.id == that.id);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
