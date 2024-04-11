/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
