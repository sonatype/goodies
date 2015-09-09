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
package org.sonatype.sisu.goodies.eventbus.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.eventbus.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link org.sonatype.sisu.goodies.eventbus.EventBus} implementation using guava event bus.
 * <p/>
 * It differs from guava event bus by dispatching events as they appear (is re-entrant). Guava will queue up all event
 * and dispatch them in the order they were posted, without re-entrance.
 *
 * @since 1.2
 */
@Named
@Singleton
public class DefaultEventBus
    implements EventBus
{

  public static final Logger LOG = LoggerFactory.getLogger(DefaultEventBus.class);

  private static final Marker REGISTRATION = MarkerFactory.getMarker("registration");

  public static final Marker DISPATCHING = MarkerFactory.getMarker("dispatching");

  private final com.google.common.eventbus.EventBus eventBus;

  @Inject
  public DefaultEventBus(final @Named("${guava.eventBus:-reentrant}") com.google.common.eventbus.EventBus eventBus) {
    this.eventBus = checkNotNull(eventBus);
    LOG.info("Using {}", eventBus);
  }

  @Override
  public EventBus register(final Object handler) {
    eventBus.register(handler);
    LOG.debug(REGISTRATION, "Registered handler '{}'", handler);
    return this;
  }

  @Override
  public EventBus unregister(final Object handler) {
    eventBus.unregister(handler);
    LOG.debug(REGISTRATION, "Unregistered handler '{}'", handler);
    return this;
  }

  @Override
  public EventBus post(final Object event) {
    LOG.debug("Event '{}' fired", event);
    eventBus.post(event);
    return this;
  }

}
