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
package org.sonatype.sisu.goodies.common.io;

import java.io.Closeable;
import java.io.IOException;

import org.sonatype.sisu.goodies.common.Loggers;
import org.sonatype.sisu.goodies.common.UnhandledThrowable;

import org.slf4j.Logger;

/**
 * Quietly closes {@link Closeable} objects.
 *
 * @since 1.0
 */
public final class Closer
{
  private static final Logger log = Loggers.getLogger(Closer.class);

  /**
   * @since 1.5
   */
  private Closer() {}

  public static void close(final Closeable... targets) {
    if (targets == null) {
      return;
    }

    for (Closeable target : targets) {
      if (target != null) {
        log.trace("Closing: {}", target);
        try {
          target.close();
        }
        catch (IOException e) {
          UnhandledThrowable.onFailure(e);
        }
      }
    }
  }
}