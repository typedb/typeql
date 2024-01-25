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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.collection.Collections.list;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.singletonList;

public class Permutations {

    /**
     * We implement the C++ STL next_permutation method of lazily generating permutations
     */
    public static <T> Iterator<List<T>> permutations(Set<T> items) {
        if (items.size() == 0) return emptyIterator();
        else if (items.size() == 1) return singletonList(list(items.iterator().next())).iterator();
        else return new PermutationIterator<>(items);
    }

    private static class PermutationIterator<T> implements Iterator<List<T>> {

        private final Map<Integer, T> mapping;
        private final int[] itemKeys;
        private boolean hasNext;

        private PermutationIterator(Set<T> items) {
            // assign a comparable ordering over the items
            mapping = new HashMap<>();
            // make the initial ordering mostly deterministic
            List<T> sortedItems = items.stream().sorted(Comparator.comparing(Object::hashCode)).collect(Collectors.toList());
            itemKeys = new int[sortedItems.size()];
            for (int i = 0; i < sortedItems.size(); i++) {
                mapping.put(i, sortedItems.get(i));
                itemKeys[i] = i;
            }
            hasNext = true;
        }

        @Override
        public List<T> next() {
            if (!hasNext()) throw new NoSuchElementException();
            // convert the keys back into the items
            List<T> permutation = new ArrayList<>(itemKeys.length);
            for (int index : itemKeys) {
                permutation.add(mapping.get(index));
            }
            hasNext = false;
            return permutation;
        }

        @Override
        public boolean hasNext() {
            if (hasNext) return true;
            // find the longest tail that is decreasing
            int tailIndex = itemKeys.length - 1;
            while (itemKeys[tailIndex] < itemKeys[tailIndex - 1]) {
                tailIndex--;
                if (tailIndex == 0) return false;
            }
            swapPreviousWithLarger(tailIndex);
            reverseTail(tailIndex);
            // itemKeys contains the lexicographical next permutation
            hasNext = true;
            return true;
        }
        // swap the previous element with the smallest element larger than it in the descending tail

        private void swapPreviousWithLarger(int tailIndex) {
            for (int swap = itemKeys.length - 1; swap >= tailIndex; swap--) {
                if (itemKeys[swap] > itemKeys[tailIndex - 1]) {
                    swap(itemKeys, swap, tailIndex - 1);
                    break;
                }
            }
        }
        // reverse the tail to get it back into increasing order

        private void reverseTail(int tailIndex) {
            for (int i = tailIndex, j = itemKeys.length - 1; i < j; i++, j--) {
                swap(itemKeys, i, j);
            }
        }

        private void swap(int[] arr, int i, int j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}
