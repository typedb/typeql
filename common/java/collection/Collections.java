/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;

public class Collections {

    @SafeVarargs
    public static <K, V> Map<K, V> map(Pair<K, V>... pairs) {
        Map<K, V> map = new HashMap<>();
        for (Pair<K, V> tuple : pairs) {
            map.put(tuple.first(), tuple.second());
        }
        return java.util.Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> map(Map<K, V> map) {
        return java.util.Collections.unmodifiableMap(map);
    }

    @SafeVarargs
    public static <T> Set<T> set(T... items) {
        return set(Arrays.asList(items));
    }

    public static <T> Set<T> set(Collection<T> collection) {
        Set<T> set = new HashSet<>(collection);
        return java.util.Collections.unmodifiableSet(set);
    }

    @SafeVarargs
    public static <T> Set<T> set(Collection<T> collection, T item, T... items) {
        Set<T> combined = new HashSet<>(collection);
        combined.add(item);
        combined.addAll(Arrays.asList(items));
        return java.util.Collections.unmodifiableSet(combined);
    }

    @SafeVarargs
    public static <T> Set<T> concatToSet(Collection<? extends T> collection, Collection<? extends T>... collections) {
        Set<T> combined = new HashSet<>(collection);
        for (Collection<? extends T> c : collections) combined.addAll(c);
        return java.util.Collections.unmodifiableSet(combined);
    }

    @SafeVarargs
    public static <T> List<T> list(T... items) {
        return java.util.Collections.unmodifiableList(Arrays.asList(items));
    }

    public static <T> List<T> list(Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        return java.util.Collections.unmodifiableList(list);
    }

    @SafeVarargs
    public static <T> List<T> list(Collection<T> collection, T item, T... array) {
        List<T> combined = new ArrayList<>(collection);
        combined.add(item);
        combined.addAll(Arrays.asList(array));
        return java.util.Collections.unmodifiableList(combined);
    }

    @SafeVarargs
    public static <T> List<T> concatToList(Collection<? extends T> collection, Collection<? extends T>... collections) {
        List<T> combined = new ArrayList<>(collection);
        for (Collection<? extends T> c : collections) combined.addAll(c);
        return java.util.Collections.unmodifiableList(combined);
    }

    public static <A, B> Pair<A, B> pair(A first, B second) {
        return new Pair<>(first, second);
    }

    public static <A, B, C> Triple<A, B, C> triple(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }

    @SafeVarargs
    public static <T> boolean containsAll(Collection<T> collection, T... values) {
        for (T value : values) {
            if (!collection.contains(value)) return false;
        }
        return true;
    }

    public static <T> boolean arrayContains(T[] values, T value) {
        for (final T v : values) {
            if (Objects.equals(value, v)) return true;
        }
        return false;
    }

    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> minSet;
        Set<T> maxSet;
        if (set1.size() < set2.size()) {
            minSet = set1;
            maxSet = set2;
        } else {
            minSet = set2;
            maxSet = set1;
        }
        Set<T> intersection = new HashSet<>();
        for (T elem : minSet) {
            if (maxSet.contains(elem)) intersection.add(elem);
        }
        return intersection;
    }

    public static <T> boolean hasIntersection(Set<T> set1, Set<T> set2) {
        Set<T> minSet;
        Set<T> maxSet;
        if (set1.size() < set2.size()) {
            minSet = set1;
            maxSet = set2;
        } else {
            minSet = set2;
            maxSet = set1;
        }
        for (T elem : minSet) {
            if (maxSet.contains(elem)) return true;
        }
        return false;
    }

    /**
     * Optimised set intersection detection when using sorted sets
     */
    public static <T extends Comparable<T>> boolean hasIntersection(NavigableSet<T> set1, NavigableSet<T> set2) {
        NavigableSet<T> active = set1;
        NavigableSet<T> other = set2;
        if (active.isEmpty()) return false;
        T currentKey = active.first();
        while (currentKey != null) {
            T otherKey = other.ceiling(currentKey);
            if (otherKey != null && otherKey.equals(currentKey)) return true;
            currentKey = otherKey;
            NavigableSet<T> tmp = other;
            other = active;
            active = tmp;
        }
        return false;
    }
}
