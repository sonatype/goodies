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
package org.sonatype.sisu.goodies.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quietly stops {@link Lifecycle} objects.
 *
 * @since 1.0
 *
 * @deprecated Use {@link Lifecycles} helpers instead.
 */
@Deprecated
public class Stopper
{
  private static final Logger log = LoggerFactory.getLogger(Stopper.class);

  private static void onFailure(final Throwable cause) {
    log.warn(cause.toString(), cause);
  }

  public static void stop(final Lifecycle... targets) {
    if (targets == null) {
      return;
    }

    for (Lifecycle target : targets) {
      if (target != null) {
        log.trace("Stopping: {}", target);
        try {
          target.stop();
        }
        catch (Exception e) {
          onFailure(e);
        }
      }
    }
  }
}