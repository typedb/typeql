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
