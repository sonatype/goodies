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

import com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Lifecycle} helpers.
 *
 * @since 1.10
 */
public class Lifecycles
{
  //
  // Starting
  //

  /**
   * Start given lifecycle and propagate exceptions.
   */
  public static void start(final Lifecycle lifecycle) {
    checkNotNull(lifecycle);

    try {
      lifecycle.start();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Start given lifecycle-aware and propagate exceptions.
   *
   * @see #start(Lifecycle)
   */
  public static void start(final LifecycleAware aware) {
    checkNotNull(aware);

    start(aware.getLifecycle());
  }

  /**
   * Start given object if it implements {@link Lifecycle} or {@link LifecycleAware}.
   *
   * @see #start(Lifecycle)
   * @see #start(LifecycleAware)
   */
  public static void start(final Object obj) {
    checkNotNull(obj);

    if (obj instanceof Lifecycle) {
      start((Lifecycle) obj);
    }
    else if (obj instanceof LifecycleAware) {
      start((LifecycleAware) obj);
    }
  }

  //
  // Stopping
  //

  /**
   * Stop given lifecycle and propagate exceptions.
   */
  public static void stop(final Lifecycle lifecycle) {
    checkNotNull(lifecycle);

    try {
      lifecycle.stop();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Stop given lifecycle-aware and propagate exceptions.
   *
   * @see #stop(Lifecycle)
   */
  public static void stop(final LifecycleAware aware) {
    checkNotNull(aware);

    stop(aware.getLifecycle());
  }

  /**
   * Stop given object if it implements {@link Lifecycle} or {@link LifecycleAware}.
   *
   * @see #stop(Lifecycle)
   * @see #stop(LifecycleAware)
   */
  public static void stop(final Object obj) {
    checkNotNull(obj);

    if (obj instanceof Lifecycle) {
      stop((Lifecycle)obj);
    }
    else if (obj instanceof LifecycleAware) {
      stop((LifecycleAware) obj);
    }
  }
}
