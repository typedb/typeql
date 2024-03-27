/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;

public class NamedForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    private final AtomicLong index = new AtomicLong(0);
    private final String prefix;

    public NamedForkJoinWorkerThreadFactory(String prefix) {
        this.prefix = prefix + "::";
    }

    public NamedForkJoinWorkerThreadFactory(Class<?> clazz, String function) {
        this(clazz.getSimpleName() + "::" + function);
    }

    public static NamedForkJoinWorkerThreadFactory create(String prefix) {
        return new NamedForkJoinWorkerThreadFactory(prefix);
    }

    public static NamedForkJoinWorkerThreadFactory create(Class<?> clazz, String function) {
        return new NamedForkJoinWorkerThreadFactory(clazz, function);
    }

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        worker.setName(prefix + worker.getPoolIndex());
        return worker;
    }
}
