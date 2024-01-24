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
