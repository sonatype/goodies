/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.sisu.goodies.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * {@link TaskThread} factory.
 *
 * @since 1.0
 */
public class TaskThreadFactory
    implements ThreadFactory
{
    private final AtomicInteger counter = new AtomicInteger(0);

    private final String prefix;

    public TaskThreadFactory(final String prefix) {
        this.prefix = checkNotNull(prefix);
    }

    private String name() {
        return format("%s-%d", prefix, counter.incrementAndGet()); //NON-NLS
    }

    public Thread newThread(final Runnable task) {
        return new TaskThread(task, name());
    }
}
