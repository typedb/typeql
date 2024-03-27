/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
