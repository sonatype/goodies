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

import org.sonatype.sisu.goodies.common.Loggers;

import org.slf4j.Logger;

import static com.google.common.base.Throwables.throwIfUnchecked;

/**
 * Adds support to capture/report handler exception failures and re-throw them.
 *
 * @since 1.10
 */
public class ThrowingGuavaEventBus
    extends EventBus
{

  protected final Logger log;

  public ThrowingGuavaEventBus() {
    this.log = Loggers.getLogger(getClass());
  }

  public ThrowingGuavaEventBus(final String identifier) {
    super(identifier);
    this.log = Loggers.getLogger(getClass() + "." + identifier);
  }
  
  void handleSubscriberException(Throwable cause, SubscriberExceptionContext context) {
    super.handleSubscriberException(cause, context);
    
    log.warn("Dispatch of event={} to handler={} failed", context.getEvent(), context.getSubscriber(), cause);
    
    throwIfUnchecked(cause);
    throw new RuntimeException(cause);
  }

  @Override
  public String toString() {
    return "Throwing Guava EventBus";
  }

}
