/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link Runnable} task executing threads.
 *
 * @since 1.0
 */
public class TaskThread
    extends ThreadSupport
{
    private final Runnable task;

    public TaskThread(final Runnable task) {
        this.task = checkNotNull(task);
    }

    public TaskThread(final Runnable task, final String name) {
        super(name);
        this.task = checkNotNull(task);
    }

    public TaskThread(final ThreadGroup group, final Runnable task, final String name) {
        super(group, name);
        this.task = checkNotNull(task);
    }

    protected void doRun() throws Exception {
        task.run();
    }
}