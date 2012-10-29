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

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.sisu.goodies.common.Mutex;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link Thread} implementations.
 *
 * @since 1.0
 */
public abstract class ThreadSupport
    extends Thread
{
    @NonNls
    protected final Logger log;

    private final Mutex lock = new Mutex();

    private volatile boolean canceled = false;

    protected ThreadSupport() {
        super();
        this.log = checkNotNull(createLogger());
    }

    protected ThreadSupport(final String name) {
        super(name);
        this.log = checkNotNull(createLogger());
    }

    protected ThreadSupport(final ThreadGroup group, final String name) {
        super(group, name);
        this.log = checkNotNull(createLogger());
    }

    protected Logger createLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    public void cancel() {
        synchronized (lock) {
            log.debug("Canceled");
            canceled = true;
            lock.notifyAll();
        }
    }

    public boolean isCanceled() {
        synchronized (lock) {
            return canceled;
        }
    }

    protected Mutex getLock() {
        return lock;
    }

    @Override
    public void run() {
        log.debug("Running");

        try {
            doRun();
        }
        catch (InterruptedException e) {
            log.warn("Interrupted", e);
            onFailure(e);
        }
        catch (Exception e) {
            log.error("Failed", e);
            onFailure(e);
        }

        log.debug("Stopping");

        try {
            doStop();
        }
        catch (Exception e) {
            log.error("Stop failed", e);
            onFailure(e);
        }

        log.debug("Stopped");
    }

    protected void onFailure(final Throwable cause) {
        // empty
    }

    protected abstract void doRun() throws Exception;

    protected void doStop() throws Exception {
        // empty
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name='" + getName() + "'}";
    }

    /**
     * Helper to build thread name.
     *
     * @return The simple-name of the given class.
     *
     * @since 1.5
     */
    public static String nameOf(final Class type) {
        checkNotNull(type);
        checkArgument(!type.isAnonymousClass());
        return type.getSimpleName();
    }

    /**
     * Helper to build thread name.
     *
     * @return The simple-name of hte given class with given suffix.
     *
     * @since 1.5
     */
    public static String nameOf(final Class type, final String suffix) {
        checkNotNull(suffix);
        return nameOf(type) + suffix;
    }
}