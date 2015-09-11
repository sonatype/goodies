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
package org.sonatype.goodies.common;

import org.sonatype.gossip.Level;

import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * When all else fails, instead of eating exceptions, give them to me.
 *
 * @since 1.5
 */
public final class UnhandledThrowable
{
  private static final Logger log = Loggers.getLogger(UnhandledThrowable.class);

  private UnhandledThrowable() {}

  private static Level getFailureLevel() {
    Level level = Level.TRACE;
    String value = Properties2.getSystemProperty(UnhandledThrowable.class, "failureLevel", level);
    try {
      return Level.valueOf(value.toUpperCase());
    }
    catch (Throwable e) {
      log.error("Invalid level: {}", value, e);
      return level;
    }
  }

  private static final Level level = getFailureLevel();

  public static void onFailure(final Throwable cause) {
    //noinspection ThrowableResultOfMethodCallIgnored
    checkNotNull(cause);
    if (level.isEnabled(log)) {
      level.log(log, cause.toString(), cause);
    }
  }
}