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
