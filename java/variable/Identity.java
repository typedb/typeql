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

package graql.lang.variable;

import graql.lang.Graql;
import graql.lang.exception.GraqlException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

abstract class Identity {

    protected final Type type;

    enum Type {NAMED, ANONYMOUS}

    Identity(Type type) {
        this.type = type;
    }

    static Identity named(String name) {
        return new Identity.Named(name);
    }

    static Identity anonymous() {
        return new Identity.Anonymous(true);
    }

    static Identity hidden() {
        return new Identity.Anonymous(false);
    }

    Identity.Type type() {
        return type;
    }

    abstract String syntax();

    abstract String identifier();

    abstract boolean isVisible();

    Identity.Named asNamed() {
        throw GraqlException.invalidCastException(this.getClass(), Identity.Named.class);
    }

    Identity.Anonymous asAnonymous() {
        throw GraqlException.invalidCastException(this.getClass(), Identity.Anonymous.class);
    }

    @Override
    public String toString() {
        return identifier();
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    static class Named extends Identity {

        private static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
        private final String name;
        private final int hash;

        Named(String name) {
            super(Type.NAMED);
            if (!REGEX.matcher(name).matches()) throw GraqlException.invalidVariableName(name, REGEX.toString());
            this.name = name;
            this.hash = Objects.hash(this.type, this.name);
        }

        Identity.Named asNamed() {
            return this;
        }

        String name() {
            return name;
        }

        @Override
        String syntax() {
            return Graql.Token.Char.$ + name;
        }

        @Override
        String identifier() {
            return syntax();
        }

        @Override
        boolean isVisible() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Named that = (Named) o;
            return this.name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    static class Anonymous extends Identity {

        private static final AtomicInteger counter = new AtomicInteger(0);
        private final String id;
        private final boolean isVisible;
        private final int hash;

        Anonymous(boolean isVisible) {
            super(Type.ANONYMOUS);
            this.id = Integer.toString(counter.getAndIncrement());
            this.isVisible = isVisible;
            this.hash = Objects.hash(this.type, this.id, this.isVisible);
        }

        Anonymous(String id) {
            super(Type.ANONYMOUS);
            this.id = id;
            this.isVisible = false;
            this.hash = Objects.hash(this.type, this.id, this.isVisible);
        }

        String name() {
            return id;
        }

        @Override
        String syntax() {
            return Graql.Token.Char.$_.toString();
        }

        @Override
        String identifier() {
            return Graql.Token.Char.$_ + id;
        }

        @Override
        boolean isVisible() {
            return isVisible;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Anonymous that = (Anonymous) o;
            return (this.id.equals(that.id) && this.isVisible == that.isVisible);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    static class Labelled extends Identity.Anonymous {

        Labelled(String label) {
            super(label);
        }
    }
}
