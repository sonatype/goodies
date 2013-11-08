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
}
