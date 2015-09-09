/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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

import org.sonatype.gossip.Level;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.Mutex;
import org.sonatype.sisu.goodies.lifecycle.LifecycleHandlerContext.MainMap;

import com.google.common.base.Throwables;

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

  /**
   * @since 1.7
   */
  protected Level getLifecycleLogLevel() {
    return Level.DEBUG;
  }

  private final class Handler
      implements LifecycleHandler
  {
    private final LifecycleSupport owner = LifecycleSupport.this;

    private Throwable failure;

    public void log(final String message) {
      getLifecycleLogLevel().log(log, message);
    }

    public boolean isFailed() {
      return failure != null;
    }

    public boolean isResettable() {
      return owner.isResettable();
    }

    public void doFailed() {
      if (failure != null) {
        owner.doFailed(failure);
      }
    }

    public void doStart() {
      try {
        owner.doStart();
      }
      catch (Throwable e) {
        failure = e;
      }
    }

    public void doStop() {
      try {
        owner.doStop();
      }
      catch (Throwable e) {
        failure = e;
      }
    }

    public void doReset() {
      failure = null;
      try {
        owner.doReset();
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

  protected void doFailed(final Throwable cause) {
    log.error("Life-cycle operation failed", cause);
    throw Throwables.propagate(cause);
  }

  protected boolean isResettable() {
    return false;
  }

  protected void doReset() throws Exception {
    // empty
  }

  private void maybeFail() {
    //  Called from start or stop, in a synchronized block already
    if (state.getOwner().isFailed()) {
      state.fail();
    }
  }

  // NOTE: We enter a beginning state, then transition to the ending state which is where the callback is
  // NOTE: invoked (sorta strange, should be sorted out) ... could probably use Entry { doXXX(); } instead?

  public final void start() throws Exception {
    synchronized (lock) {
      state.start();
      state.started();
      maybeFail();
    }
  }

  protected void doStart() throws Exception {
    // empty
  }

  public final void stop() throws Exception {
    synchronized (lock) {
      state.stop();
      state.stopped();
      maybeFail();
    }
  }

  protected void doStop() throws Exception {
    // empty
  }

  // FIXME: Need to sort out the synchronization locks here...
  // FIXME: Some parts of 'state' are sync, some aren't like getState()
  // FIXME: For now use our lock & pretend that 'state' is not sync'd at all

  protected boolean isStarted() {
    synchronized (lock) {
      return state.getState().equals(MainMap.Started);
    }
  }

  protected void ensureStarted() {
    checkState(isStarted(), "Not started");
  }

  protected boolean isStopped() {
    synchronized (lock) {
      return state.getState().equals(MainMap.Stopped);
    }
  }

  protected void ensureStopped() {
    checkState(isStopped(), "Not stopped");
  }
}