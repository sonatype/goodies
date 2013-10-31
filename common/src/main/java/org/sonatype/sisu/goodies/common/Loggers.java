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
package org.sonatype.sisu.goodies.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logger utilities.
 *
 * @since 1.7.3
 */
public final class Loggers
{
  private static final String GUICE_ENHANCED = "$$EnhancerByGuice$$";

  private static boolean isEnhancedSubclass(final Class type) {
    return type.getName().contains(GUICE_ENHANCED);
  }

  /**
   * Helper to create a logger and deal with class-names created by AOP platforms.
   */
  public static Logger getLogger(final Class type) {
    checkNotNull(type);
    if (isEnhancedSubclass(type)) {
      return LoggerFactory.getLogger(type.getSuperclass());
    }
    return LoggerFactory.getLogger(type);
  }

  public static Logger getLogger(final Object obj) {
    checkNotNull(obj);
    return getLogger(obj.getClass());
  }

  public static Logger getLogger(final String name) {
    checkNotNull(name);
    return LoggerFactory.getLogger(name);
  }
}
