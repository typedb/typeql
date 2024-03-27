/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {

    private final AtomicLong index = new AtomicLong(0);
    private final String prefix;

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix + "::";
    }

    public NamedThreadFactory(Class<?> clazz, String function) {
        this(clazz.getSimpleName() + "::" + function);
    }

    public static NamedThreadFactory create(String prefix) {
        return new NamedThreadFactory(prefix);
    }

    public static NamedThreadFactory create(Class<?> clazz, String function) {
        return new NamedThreadFactory(clazz, function);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(prefix + index.getAndIncrement());
        return thread;
    }
}
