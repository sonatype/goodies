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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.gossip.Level;

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Simple {@link Lifecycle} support.
 *
 * @since 2.0.1
 */
public class SimpleLifecycleSupport
    extends ComponentSupport
    implements Lifecycle
{
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  private enum State
  {
    NEW, STARTED, STOPPED, FAILED
  }

  private State current = State.NEW;

  @Override
  public Lifecycle getLifecycle() {
    return this;
  }

  /**
   * Returns the logger level for transition messages.
   */
  protected Level getLifecycleLogLevel() {
    return Level.DEBUG;
  }

  /**
   * Log transition messages.
   */
  private void log(final String message) {
    getLifecycleLogLevel().log(log, message);
  }

  /**
   * Attempt to lock.
   */
  private Lock lock(final Lock lock) {
    checkNotNull(lock);
    try {
      lock.tryLock(60, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
    return lock;
  }

  /**
   * Returns locked read lock.
   */
  private Lock readLock() {
    return lock(readWriteLock.readLock());
  }

  /**
   * Returns locked write lock.
   */
  private Lock writeLock() {
    return lock(readWriteLock.writeLock());
  }

  /**
   * Check if current state is given state.
   */
  private boolean is(final State state) {
    Lock lock = readLock();
    try {
      return current == state;
    }
    finally {
      lock.unlock();
    }
  }

  /**
   * Ensure current state is one of allowed states.
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
    Lock lock = writeLock();
    ensure(State.NEW, State.STOPPED);
    try {
      log("Starting");
      doStart();
      current = State.STARTED;
      log("Started");
    }
    catch (Throwable failure) {
      doFailed(failure);
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
    Lock lock = writeLock();
    ensure(State.STARTED);
    try {
      log("Stopping");
      doStop();
      current = State.STOPPED;
      log("Stopped");
    }
    catch (Throwable failure) {
      doFailed(failure);
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

  protected void doFailed(final Throwable cause) {
    log.error("Lifecycle operation failed", cause);
    current = State.FAILED;
    throw Throwables.propagate(cause);
  }

  protected boolean isFailed() {
    return is(State.FAILED);
  }
}
