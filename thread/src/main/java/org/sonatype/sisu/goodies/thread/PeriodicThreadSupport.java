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

import org.sonatype.sisu.goodies.common.Time;
import org.sonatype.sisu.goodies.lifecycle.Lifecycle;
import org.sonatype.sisu.goodies.lifecycle.LifecycleAware;

/**
 * Support for periodic {@link Thread} implementations.
 *
 * @since 1.0
 */
public abstract class PeriodicThreadSupport
    extends ThreadSupport
    implements LifecycleAware
{
    protected PeriodicThreadSupport() {
        super();
    }

    protected PeriodicThreadSupport(final String name) {
        super(name);
    }

    protected PeriodicThreadSupport(final ThreadGroup group, final String name) {
        super(group, name);
    }

    @Override
    protected final void doRun() throws Exception {
        while (true) {
            doTask();

            synchronized (getLock()) {
                if (!isCanceled()) {
                    doContinue();
                }

                if (isCanceled()) {
                    break;
                }
            }

            log.trace("Continuing");
        }
    }

    protected abstract void doTask() throws Exception;

    protected void doContinue() throws Exception {
        // empty
    }

    protected void pause(final Time time) throws InterruptedException {
        synchronized (getLock()) {
            log.trace("Pausing for: {}", time);
            time.wait(getLock());
        }
    }

    public Lifecycle getLifecycle() {
        return new Lifecycle()
        {
            public void start() throws Exception {
                PeriodicThreadSupport.this.start();
            }

            public void stop() throws Exception {
                cancel();
                join();
            }

            public Lifecycle getLifecycle() {
                return this;
            }
        };
    }
}