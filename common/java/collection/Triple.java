/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
