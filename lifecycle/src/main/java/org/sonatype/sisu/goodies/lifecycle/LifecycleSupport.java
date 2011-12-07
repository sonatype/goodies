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
package org.sonatype.sisu.goodies.lifecycle;

import com.google.common.base.Throwables;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.Mutex;
import org.sonatype.sisu.goodies.lifecycle.LifecycleHandlerContext.MainMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * Support for {@link Lifecycle} implementations.
 *
 * @since 1.0
 */
public class LifecycleSupport
    extends ComponentSupport
    implements Lifecycle
{
    private final Mutex lock = new Mutex();
    
    // FIXME: Sort out how to best use SMC-generated statemachine, this stuff is a bit wonky atm

    private final class Handler
        implements LifecycleHandler
    {
        private Throwable failure;

        public Throwable getFailure() {
            return failure;
        }

        public boolean isResettable() {
            return LifecycleSupport.this.isResettable();
        }

        public void doStart() {
            try {
                LifecycleSupport.this.doStart();
            }
            catch (Throwable e) {
                failure = e;
            }
        }

        public void doStop() {
            try {
                LifecycleSupport.this.doStop();
            }
            catch (Throwable e) {
                failure = e;
            }
        }

        public void doReset() {
            failure = null;
            try {
                LifecycleSupport.this.doReset();
            }
            catch (Throwable e) {
                failure = e;
            }
        }
    }

    private final LifecycleHandlerContext state = new LifecycleHandlerContext(new Handler());

    public Lifecycle getLifecycle() {
        return this;
    }

    protected void onFailure(final Throwable cause) {
        log.error("Life-cycle operation failed", cause);
    }

    private void throwFailureIfFailed() throws Exception {
        Throwable failure = state.getOwner().getFailure();
        if (failure != null) {
            onFailure(failure);
            state.fail();
            Throwables.propagateIfInstanceOf(failure, Exception.class);
            throw Throwables.propagate(failure);
        }
    }

    protected boolean isResettable() {
        return false;
    }
    
    public final void start() throws Exception {
        synchronized (lock) {
            log.debug("Starting");
            state.start();
            state.started();
            throwFailureIfFailed();
            log.debug("Started");
        }
    }

    protected void doStart() throws Exception {
        // empty
    }

    public final void stop() throws Exception {
        synchronized (lock) {
            log.debug("Stopping");
            state.stop();
            state.stopped();
            throwFailureIfFailed();
            log.debug("Stopped");
        }
    }

    protected void doStop() throws Exception {
        // empty
    }

    protected void doReset() throws Exception {
        // empty
    }

    protected boolean isStarted() {
        return state.getState().equals(MainMap.Started);
    }

    protected void ensureStarted() {
        checkState(isStarted());
    }

    protected boolean isStopped() {
        return state.getState().equals(MainMap.Stopped);
    }

    protected void ensureStopped() {
        checkState(isStopped());
    }
}