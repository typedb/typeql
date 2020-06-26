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


package graql.lang.statement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A Label is composed of a Name and an optional Scope
 */
public class Label implements Comparable<Label>, Serializable {
    private static final long serialVersionUID = 2051578406740868932L;

    private final String name;
    private final String scope;
    private final String scopedName;

    public Label(String name, @Nullable String scope) {
        if (name == null) {
            throw new NullPointerException("Null value");
        }
        this.name = name;
        this.scope = scope;
        if (scope == null) {
            scopedName = name;
        } else {
            scopedName = scope + ":" + name;
        }
    }

    /**
     * @param name The string which potentially represents a Type
     * @return The matching Type Label
     */
    @CheckReturnValue
    public static Label of(String name) {
        return new Label(name, null);
    }

    /**
     * @param name The string which potentially represents a Type
     * @param scope The scope for this type
     * @return The matching Type Label
     */
    @CheckReturnValue
    public static Label of(String name, String scope) {
        return new Label(name, scope);
    }

    public String name() {
        return name;
    }

    public String scope() {
        return scope;
    }

    public String scopedName() {
        return scopedName;
    }

    @Override
    public int compareTo(Label o) {
        return scopedName.compareTo(o.scopedName());
    }

    @Override
    public final String toString() {
        return scopedName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Label) {
            Label that = (Label) o;
            return (this.scopedName.equals(that.scopedName()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.scopedName.hashCode();
        return h;
    }
}