/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.eventbus.internal.DefaultEventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 1.5
 */
@Named("default")
@Singleton
public class DefaultGuavaEventBus
    extends EventBus
{

  public DefaultGuavaEventBus() {
    super(new LoggingSubscriberExceptionHandler("default"));
  }

  @Override
  public void dispatch(final Object event, final EventSubscriber wrapper) {
    DefaultEventBus.LOG.trace(DefaultEventBus.DISPATCHING, "Dispatching '{}' to {}", event, wrapper);
    super.dispatch(event, wrapper);
  }

  @Override
  public String toString() {
    return "Default Guava EventBus";
  }

  private static final class LoggingSubscriberExceptionHandler
      implements SubscriberExceptionHandler
  {

    /**
     * Logger for event dispatch failures.  Named by the fully-qualified name of
     * this class, followed by the identifier provided at construction.
     */
    private final Logger logger;

    /**
     * @param identifier a brief name for this bus, for logging purposes. Should
     *                   be a valid Java identifier.
     */
    public LoggingSubscriberExceptionHandler(final String identifier) {
      logger = LoggerFactory.getLogger(EventBus.class.getName() + "." + checkNotNull(identifier));
    }

    @Override
    public void handleException(final Throwable exception, final SubscriberExceptionContext context) {
      logger.error(
          "Could not dispatch event: {} to {}",
          context.getSubscriber(), context.getSubscriberMethod(), exception
      );
    }
  }

}
