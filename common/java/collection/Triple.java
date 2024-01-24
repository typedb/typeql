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
 */

package com.vaticle.typedb.common.collection;

import java.util.Objects;

public class Triple<FIRST, SECOND, THRID> {

    private final FIRST first;
    private final SECOND second;
    private final THRID third;
    private final int hash;

    public Triple(FIRST first, SECOND second, THRID third) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.hash = Objects.hash(this.first, this.second, this.third);
    }

    public FIRST first() {
        return first;
    }

    public SECOND second() {
        return second;
    }

    public THRID third() {
        return third;
    }

    @Override
    public String toString() {
        return String.format("Triple: {%s, %s, %s", first.toString(), second.toString(), third.toString());
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Triple<?, ?, ?> other = (Triple) obj;
        return (Objects.equals(this.first, other.first) &&
                Objects.equals(this.second, other.second) &&
                Objects.equals(this.third, other.third));
    }

    public int hashCode() {
        return hash;
    }
}
