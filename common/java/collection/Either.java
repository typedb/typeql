/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.collection;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Either<FIRST, SECOND> {

    private final FIRST first;
    private final SECOND second;
    private final int hash;

    private Either(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
        this.hash = Objects.hash(first, second);
    }

    public static <T, U> Either<T, U> first(T first) {
        return new Either<>(first, null);
    }

    public static <T, U> Either<T, U> second(U second) {
        return new Either<>(null, second);
    }

    public boolean isFirst() {
        return first != null;
    }

    public boolean isSecond() {
        return second != null;
    }

    public FIRST first() {
        return first;
    }

    public SECOND second() {
        return second;
    }

    public void ifFirst(Consumer<FIRST> function) {
        if (isFirst()) function.accept(first);
    }

    public void ifSecond(Consumer<SECOND> function) {
        if (isSecond()) function.accept(second);
    }

    public <V> V apply(Function<FIRST, V> firstFn, Function<SECOND, V> secondFn) {
        if (isFirst()) return firstFn.apply(first);
        else return secondFn.apply(second);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        if (o == this) return true;
        Either that = (Either) o;
        return (Objects.equals(this.first, that.first) &&
                Objects.equals(this.second, that.second));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
