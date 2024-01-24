/*
 * Copyright (C) 2022 Vaticle
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
 *
 */

package com.vaticle.typedb.common.collection;

import java.util.Objects;

public class Pair<FIRST, SECOND> {

    private final FIRST first;
    private final SECOND second;
    private final int hash;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
        this.hash = Objects.hash(this.first, this.second);
    }

    public FIRST first() {
        return first;
    }

    public SECOND second() {
        return second;
    }

    @Override
    public String toString() {
        return String.format("pair(%s, %s)", first.toString(), second.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        if (obj == this) return true;
        Pair<?, ?> that = (Pair<?, ?>) obj;
        return Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}