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
package org.sonatype.goodies.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Lock helpers.
 *
 * @since 2.1
 */
public class Locks
{
  private Locks() {
    // empty
  }

  /**
   * Returns locked lock.
   *
   * Uses {@link Lock#tryLock} with timeout of 60 seconds to avoid potential deadlocks.
   */
  public static Lock lock(final Lock lock) {
    checkNotNull(lock);
    try {
      if (!lock.tryLock(60, TimeUnit.SECONDS)) {
        throw new RuntimeException("Failed to obtain lock after 60 seconds");
      }
    }
    catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
    return lock;
  }

  /**
   * Returns locked read-lock.
   */
  public static Lock read(final ReadWriteLock readWriteLock) {
    checkNotNull(readWriteLock);
    return lock(readWriteLock.readLock());
  }

  /**
   * Returns locked write-lock.
   */
  public static Lock write(final ReadWriteLock readWriteLock) {
    checkNotNull(readWriteLock);
    return lock(readWriteLock.writeLock());
  }
}
