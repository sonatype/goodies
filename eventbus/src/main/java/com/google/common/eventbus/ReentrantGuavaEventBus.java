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

package com.google.common.eventbus;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Named;
import javax.inject.Singleton;

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
   * Queues of events for the current thread to dispatch.
   */
  private final ThreadLocal<Queue<EventWithSubscriber>> eventsToDispatch =
      new ThreadLocal<Queue<EventWithSubscriber>>()
      {
        @Override
        protected Queue<EventWithSubscriber> initialValue() {
          return new LinkedList<EventWithSubscriber>();
        }
      };

  @Override
  void enqueueEvent(Object event, EventSubscriber subscriber) {
    eventsToDispatch.get().offer(new EventWithSubscriber(event, subscriber));
  }

  @Override
  void dispatchQueuedEvents() {
    Queue<EventWithSubscriber> events = eventsToDispatch.get();
    eventsToDispatch.remove();
    EventWithSubscriber eventWithSubscriber;
    while ((eventWithSubscriber = events.poll()) != null) {
      dispatch(eventWithSubscriber.event, eventWithSubscriber.subscriber);
    }
  }

  @Override
  public String toString() {
    return "Reentrant Guava EventBus";
  }

}
