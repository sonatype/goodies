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
package org.sonatype.sisu.goodies.lifecycle;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.Mutex;
import org.sonatype.sisu.goodies.lifecycle.internal.LifecycleManagerImpl;
import org.slf4j.Logger;

import static org.sonatype.sisu.goodies.lifecycle.Starter.start;

/**
 * Manages a shutdown hook to trigger {@link Lifecycle#stop} on all registered components.
 *
 * @since 1.0
 */
public class LifecycleShutdownManager
    extends ComponentSupport
    implements LifecycleContainer
{
    private final Mutex lock = new Mutex();

    private final ShutdownHook hook = new ShutdownHook();

    private final LifecycleManager lifecycles;

    private int count = 0;

    private boolean installed = false;

    public LifecycleShutdownManager() {
        this.lifecycles = new LifecycleManagerImpl()
        {
            @Override
            protected Logger createLogger() {
                //noinspection AccessStaticViaInstance
                return LifecycleShutdownManager.this.log;
            }
        };
        // need to begin in started state
        start(lifecycles);
    }

    private void install() {
        // only install if not already installed and there is one component
        if (installed || count != 1) return;
        log.debug("Installing hook");
        Runtime.getRuntime().addShutdownHook(hook);
        installed = true;
    }

    private void remove() {
        // only remove if not installed and there are no components
        if (!installed || count != 0) return;
        log.debug("Removing hook");
        Runtime.getRuntime().removeShutdownHook(hook);
        installed = false;
    }

    public <T extends LifecycleAware> T add(final T component) {
        synchronized (lock) {
            T value = lifecycles.add(component);
            count++;
            install();
            return value;
        }
    }

    public LifecycleShutdownManager add(final LifecycleAware... components) {
        synchronized (lock) {
            lifecycles.add(components);
            count++;
            install();
        }
        return this;
    }

    public <T extends LifecycleAware> T remove(final T component) {
        synchronized (lock) {
            T value = lifecycles.remove(component);
            count--;
            remove();
            return value;
        }
    }

    public LifecycleShutdownManager remove(final LifecycleAware... components) {
        synchronized (lock) {
            lifecycles.remove(components);
            count--;
            remove();
        }
        return this;
    }

    public void clear() {
        synchronized (lock) {
            lifecycles.clear();
            count = 0;
            remove();
        }
    }

    private class ShutdownHook
        extends Thread
    {
        public ShutdownHook() {
            setName(getClass().getSimpleName());
        }

        @Override
        public void run() {
            try {
                lifecycles.stop();
            }
            catch (Exception e) {
                log.error("Stop failed", e);
            }
        }
    }
}