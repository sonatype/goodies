/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.lifecycle;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.goodies.common.Locks;

import com.google.common.annotations.VisibleForTesting;
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
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  @VisibleForTesting
  enum State
  {
    NEW, STARTED, STOPPED, FAILED
  }

  private State current = State.NEW;

  /**
   * Log transition messages.
   *
   * @since 2.1
   */
  protected void logTransition(final String message) {
    log.debug(message);
  }

  /**
   * Log transition failure messages.
   *
   * @since 2.1
   */
  protected void logTransitionFailure(final String message, final Throwable cause) {
    log.error(message, cause);
  }

  /**
   * Check if current state is given state.
   */
  @VisibleForTesting
  boolean is(final State state) {
    Lock lock = Locks.read(readWriteLock);
    try {
      return current == state;
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * Ensure current state is one of allowed states.
   *
   * Must be called within scope of lock.
   */
  private void ensure(final State... allowed) {
    for (State allow : allowed) {
      if (current == allow) {
        return;
      }
    }

    throw new IllegalStateException("Invalid state: " + current + "; allowed: " + Arrays.toString(allowed));
  }

  //
  // Start
  //

  @Override
  public final void start() throws Exception {
    Lock lock = Locks.write(readWriteLock);
    ensure(State.NEW, State.STOPPED);
    try {
      logTransition("Starting");
      doStart();
      current = State.STARTED;
      logTransition("Started");
    }
    catch (Throwable failure) {
      doFailed("start", failure);
    }
    finally {
      lock.unlock();
    }
  }

  protected void doStart() throws Exception {
    // empty
  }

  protected boolean isStarted() {
    return is(State.STARTED);
  }

  protected void ensureStarted() {
    checkState(isStarted(), "Not started");
  }

  //
  // Stop
  //

  @Override
  public final void stop() throws Exception {
    Lock lock = Locks.write(readWriteLock);
    ensure(State.STARTED);
    try {
      logTransition("Stopping");
      doStop();
      current = State.STOPPED;
      logTransition("Stopped");
    }
    catch (Throwable failure) {
      doFailed("stop", failure);
    }
    finally {
      lock.unlock();
    }
  }

  protected void doStop() throws Exception {
    // empty
  }

  protected boolean isStopped() {
    return is(State.STOPPED);
  }

  protected void ensureStopped() {
    checkState(isStopped(), "Not stopped");
  }

  //
  // Failed
  //

  protected void doFailed(final String operation, final Throwable cause) throws Exception {
    logTransitionFailure("Lifecycle operation " + operation + " failed", cause);
    current = State.FAILED;
    Throwables.propagateIfPossible(cause, Exception.class);
    throw Throwables.propagate(cause);
  }

  /**
   * @since 2.1
   */
  protected boolean isFailed() {
    return is(State.FAILED);
  }
}
