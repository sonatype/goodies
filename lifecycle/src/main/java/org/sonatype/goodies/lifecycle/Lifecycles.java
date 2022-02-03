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

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Lifecycle} helpers.
 *
 * @since 1.10
 */
public class Lifecycles
{
  private static final Logger log = LoggerFactory.getLogger(Lifecycles.class);

  private Lifecycles() {
    // empty
  }

  //
  // Starting
  //

  /**
   * Start given lifecycle and propagate exceptions.
   */
  public static void start(final Lifecycle component) {
    checkNotNull(component);
    try {
      component.start();
    }
    catch (Exception e) {
      throw propagate(e);
    }
  }

  /**
   * Start given lifecycle-aware and propagate exceptions.
   *
   * @see #start(Lifecycle)
   */
  public static void start(final LifecycleAware component) {
    checkNotNull(component);
    start(component.getLifecycle());
  }

  /**
   * Start given component if it implements {@link Lifecycle} or {@link LifecycleAware}.
   *
   * @see #start(Lifecycle)
   * @see #start(LifecycleAware)
   */
  public static void start(final Object component) {
    if (component instanceof Lifecycle) {
      start((Lifecycle) component);
    }
    else if (component instanceof LifecycleAware) {
      start((LifecycleAware) component);
    }
    else {
      log.warn("Unable to start component; not a lifecycle: {}", component);
    }
  }

  //
  // Stopping
  //

  /**
   * Stop given lifecycle and propagate exceptions.
   */
  public static void stop(final Lifecycle component) {
    checkNotNull(component);
    try {
      component.stop();
    }
    catch (Exception e) {
      throw propagate(e);
    }
  }

  /**
   * Stop given lifecycle-aware and propagate exceptions.
   *
   * @see #stop(Lifecycle)
   */
  public static void stop(final LifecycleAware component) {
    checkNotNull(component);
    stop(component.getLifecycle());
  }

  /**
   * Stop given component if it implements {@link Lifecycle} or {@link LifecycleAware}.
   *
   * @see #stop(Lifecycle)
   * @see #stop(LifecycleAware)
   */
  public static void stop(final Object component) {
    if (component instanceof Lifecycle) {
      stop((Lifecycle)component);
    }
    else if (component instanceof LifecycleAware) {
      stop((LifecycleAware) component);
    }
    else {
      log.warn("Unable to stop component; not a lifecycle: {}", component);
    }
  }

  private static RuntimeException propagate(Throwable throwable) {
    Throwables.throwIfUnchecked(throwable);
    throw new RuntimeException(throwable);
  }
}
