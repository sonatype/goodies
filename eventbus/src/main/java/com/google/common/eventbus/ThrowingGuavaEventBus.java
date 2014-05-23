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

import java.lang.reflect.InvocationTargetException;

import org.sonatype.sisu.goodies.common.Loggers;

import com.google.common.base.Throwables;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;

/**
 * Adds support to capture/report handler exception failures and re-throw them.
 *
 * @since 1.10
 */
public class ThrowingGuavaEventBus
    extends EventBus
{

  @NonNls
  protected final Logger log;

  public ThrowingGuavaEventBus() {
    this.log = Loggers.getLogger(getClass());
  }

  public ThrowingGuavaEventBus(final String identifier) {
    super(identifier);
    this.log = Loggers.getLogger(getClass() + "." + identifier);
  }

  @Override
  void dispatch(final Object event, final EventSubscriber wrapper) {
    try {
      wrapper.handleEvent(event);
    }
    catch (InvocationTargetException e) {
      handleDispatchFailure(event, wrapper, e);
    }
  }

  protected void handleDispatchFailure(final Object event, final EventSubscriber wrapper,
                                       final InvocationTargetException cause)
  {
    log.warn("Dispatch of event={} to handler={} failed", event, wrapper, cause);
    throw Throwables.propagate(cause.getTargetException());
  }

  @Override
  public String toString() {
    return "Throwing Guava EventBus";
  }

}
