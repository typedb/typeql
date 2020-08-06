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

public abstract class Identity {

    protected final Type type;

    Identity(Type type) {
        this.type = type;
    }

    public static Identity named(String name) {
        return new Identity.Named(name);
    }

    public static Identity anonymous() {
        return new Identity.Anonymous(true);
    }

    public static Identity hidden() {
        return new Identity.Anonymous(false);
    }

    public Identity.Type type() {
        return type;
    }

    public abstract String syntax();

    public abstract String identifier();

    public abstract boolean isVisible();

    public Identity.Named asNamed() {
        throw GraqlException.invalidCastException(this.getClass(), Identity.Named.class);
    }

    public Identity.Anonymous asAnonymous() {
        throw GraqlException.invalidCastException(this.getClass(), Identity.Anonymous.class);
    }

    enum Type {NAMED, ANONYMOUS}

    public static class Named extends Identity {

        private static final Pattern REGEX = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_-]*");
        private final String name;
        private final int hash;

        Named(String name) {
            super(Type.NAMED);
            if (!REGEX.matcher(name).matches()) throw GraqlException.invalidVariableName(name, REGEX.toString());
            this.name = name;
            this.hash = Objects.hash(this.type, this.name);
        }

        public Identity.Named asNamed() {
            return this;
        }

        public String name() {
            return name;
        }

        @Override
        public String syntax() {
            return Graql.Token.Char.$ + name;
        }

        @Override
        public String identifier() {
            return syntax();
        }

        @Override
        public boolean isVisible() {
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

    public static class Anonymous extends Identity {

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

        public String name() {
            return id;
        }

        @Override
        public String syntax() {
            return Graql.Token.Char.$_.toString();
        }

        @Override
        public String identifier() {
            return Graql.Token.Char.$_ + id;
        }

        @Override
        public boolean isVisible() {
            return isVisible;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Anonymous that = (Anonymous) o;
            return (this.id == that.id && this.isVisible == that.isVisible);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
