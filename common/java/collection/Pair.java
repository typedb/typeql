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
