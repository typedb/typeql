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
