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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentSet<E> implements Set<E> {

    ConcurrentHashMap.KeySetView<E, Boolean> concurrentSet;

    public ConcurrentSet() {
        concurrentSet = ConcurrentHashMap.newKeySet();
    }

    @Override
    public int size() {
        return concurrentSet.size();
    }

    @Override
    public boolean isEmpty() {
        return concurrentSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return concurrentSet.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return concurrentSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return concurrentSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return concurrentSet.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return concurrentSet.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return concurrentSet.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return concurrentSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return concurrentSet.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return concurrentSet.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return concurrentSet.removeAll(c);
    }

    @Override
    public void clear() {
        concurrentSet.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConcurrentSet<?> that = (ConcurrentSet<?>) o;
        return this.concurrentSet.equals(that.concurrentSet);
    }

    @Override
    public int hashCode() {
        return concurrentSet.hashCode();
    }
}
