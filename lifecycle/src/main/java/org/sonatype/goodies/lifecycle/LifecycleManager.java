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

import java.util.concurrent.CopyOnWriteArrayList;

import org.sonatype.goodies.common.MultipleFailures;
import org.sonatype.goodies.common.MultipleFailures.MultipleFailuresException;

import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Manages a set of {@link Lifecycle} components.
 *
 * @since 2.0.1
 */
public class LifecycleManager
    extends LifecycleSupport
{
  private final CopyOnWriteArrayList<Lifecycle> components = new CopyOnWriteArrayList<>();

  public void add(final Lifecycle component) {
    checkNotNull(component);
    boolean added = components.addIfAbsent(component);
    if (added) {
      log.trace("Added: {}", component);
    }
  }

  public void add(final Lifecycle... components) {
    checkNotNull(components);
    for (Lifecycle component : components) {
      add(component);
    }
  }

  public void add(final LifecycleAware... components) {
    checkNotNull(components);
    for (LifecycleAware component : components) {
      add(component.getLifecycle());
    }
  }

  public void remove(final Lifecycle component) {
    checkNotNull(component);
    boolean removed = components.remove(component);
    if (removed) {
      log.trace("Removed: {}", component);
    }
  }

  public void remove(final Lifecycle... components) {
    checkNotNull(components);
    for (Lifecycle component : components) {
      remove(component);
    }
  }

  public void remove(final LifecycleAware... components) {
    checkNotNull(components);
    for (LifecycleAware component : components) {
      remove(component.getLifecycle());
    }
  }

  public int size() {
    return components.size();
  }

  public void clear() {
    components.clear();
    log.trace("Cleared");
  }

  /**
   * Start all managed components.
   *
   * Components are started in the order added.
   *
   * @throws MultipleFailuresException
   */
  @Override
  protected void doStart() throws Exception {
    int count = components.size();
    log.debug("Starting {} components", count);

    MultipleFailures failures = new MultipleFailures(count);
    for (Lifecycle component : components) {
      try {
        component.start();
      }
      catch (Throwable failure) {
        logTransitionFailure("Failed to start component: " + component, failure);
        failures.add(failure);
      }
    }
    failures.maybePropagate("Failed to start " + failures.size() + " components");
  }

  /**
   * Stop all managed components.
   *
   * Stop order is reverse of start order.
   *
   * @throws MultipleFailuresException
   */
  @Override
  protected void doStop() throws Exception {
    int count = components.size();
    log.debug("Stopping {} components", count);

    MultipleFailures failures = new MultipleFailures(count);
    for (Lifecycle component : Lists.reverse(components)) {
      try {
        component.stop();
      }
      catch (Throwable failure) {
        logTransitionFailure("Failed to stop component: " + component, failure);
        failures.add(failure);
      }
    }
    failures.maybePropagate("Failed to stop " + failures.size() + " components");
  }
}
