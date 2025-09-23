/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.common;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Throwable utilities.
 *
 * @since 1.7.4
 */
public final class Throwables2
{
  /**
   * Explain an exception and its causal-chain tersely.
   */
  public static String explain(final Throwable throwable) {
    checkNotNull(throwable);

    StringBuilder buff = new StringBuilder();
    explain(buff, throwable);

    Throwable cause = throwable;
    while ((cause = cause.getCause()) != null) {
      buff.append(", caused by: ");
      explain(buff, cause);
    }

    return buff.toString();
  }

  private static void explain(final StringBuilder buff, final Throwable throwable) {
    buff.append(throwable.getClass().getName());
    String msg = throwable.getMessage();
    if (msg != null) {
      // if there is a message, check to see if is the same as the cause and only include if its different
      Throwable cause = throwable.getCause();
      // handles Throwable(Throwable) case where message is set to cause.toString()
      if (cause == null || !msg.equals(cause.toString())) {
        buff.append(": ").append(msg);
      }
    }
  }

  /**
   * Helper to composite suppressed exceptions onto given throwable and throw.
   */
  public static <T extends Throwable> T composite(final T root, final Throwable... suppressed)
      throws T
  {
    checkNotNull(suppressed);
    for (Throwable t : suppressed) {
      root.addSuppressed(t);
    }
    throw root;
  }

  /**
   * Helper to composite suppressed exceptions onto given throwable and throw.
   */
  public static <T extends Throwable> T composite(final T root, final Collection<? extends Throwable> suppressed)
      throws T
  {
    checkNotNull(suppressed);
    for (Throwable t : suppressed) {
      root.addSuppressed(t);
    }
    throw root;
  }
}
