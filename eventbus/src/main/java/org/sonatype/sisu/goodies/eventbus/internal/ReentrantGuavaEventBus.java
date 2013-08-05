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

package org.sonatype.sisu.goodies.eventbus.internal;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.eventbus.internal.guava.EventBus;
import org.sonatype.sisu.goodies.eventbus.internal.guava.EventHandler;

import com.google.common.collect.Lists;

/**
 * A Guava {@link EventBus} that differs from default one by dispatching events as they appear (is re-entrant).
 * Guava will queue up all event and dispatch them in the order they were posted, without re-entrance.
 *
 * @since 1.5
 */
@Named("reentrant")
@Singleton
public class ReentrantGuavaEventBus
    extends DefaultGuavaEventBus
{

  /**
   * List of events for the current thread to dispatch
   */
  private final ThreadLocal<List<EventWithHandler>> eventsToDispatch = new ThreadLocal<List<EventWithHandler>>();

  @Override
  protected void enqueueEvent(final Object event, final EventHandler handler) {
    if (eventsToDispatch.get() == null) {
      eventsToDispatch.set(Lists.<EventWithHandler>newArrayList());
    }
    eventsToDispatch.get().add(new EventWithHandler(event, handler));
  }

  @Override
  protected void dispatchQueuedEvents() {
    final List<EventWithHandler> eventWithHandlers = eventsToDispatch.get();
    if (eventWithHandlers != null) {
      eventsToDispatch.remove();
      for (final EventWithHandler eventWithHandler : eventWithHandlers) {
        dispatch(eventWithHandler.event, eventWithHandler.handler);
      }
    }
  }

  @Override
  public String toString() {
    return "Reentrant Guava EventBus";
  }

}
