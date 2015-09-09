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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link LifecycleManager} implementation.
 *
 * @since 1.0
 */
public class LifecycleManagerImpl
    extends LifecycleSupport
    implements LifecycleManager
{
  private final List<Lifecycle> components = new CopyOnWriteArrayList<Lifecycle>();

  public <T extends Lifecycle> T add(final T component) {
    checkNotNull(component);
    if (!components.contains(component)) {
      components.add(component);
    }
    return component;
  }

  public <T extends LifecycleAware> T add(final T component) {
    checkNotNull(component);
    add(component.getLifecycle());
    return component;
  }

  public LifecycleManager add(final LifecycleAware... components) {
    checkNotNull(components);
    for (LifecycleAware component : components) {
      add(component);
    }
    return this;
  }

  public <T extends Lifecycle> T remove(final T component) {
    checkNotNull(component);
    components.remove(component);
    return component;
  }

  public <T extends LifecycleAware> T remove(final T component) {
    checkNotNull(component);
    remove(component.getLifecycle());
    return component;
  }

  public LifecycleManager remove(final LifecycleAware... components) {
    checkNotNull(components);
    for (LifecycleAware component : components) {
      remove(component);
    }
    return this;
  }

  public void clear() {
    components.clear();
  }

  @Override
  protected void doStart() throws Exception {
    log.debug("Starting {} components", components.size());

    int failed = 0;
    for (Lifecycle component : components) {
      try {
        component.start();
      }
      catch (Exception e) {
        failed++;
        log.error("Failed to start component: {}", component, e);
      }
    }
    if (failed != 0) {
      throw new Exception("Failed to start " + failed + " components");
    }
  }

  @Override
  protected void doStop() throws Exception {
    log.debug("Stopping {} components", components.size());

    int failed = 0;
    for (Lifecycle component : Lists.reverse(components)) {
      try {
        component.stop();
      }
      catch (Exception e) {
        failed++;
        log.error("Failed to stop component: {}", component, e);
      }
    }
    if (failed != 0) {
      throw new Exception("Failed to stop " + failed + " components");
    }
  }
}
