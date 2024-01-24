/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
