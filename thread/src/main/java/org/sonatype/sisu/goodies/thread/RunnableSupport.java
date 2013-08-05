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

package org.sonatype.sisu.goodies.thread;

import org.sonatype.sisu.goodies.common.ComponentSupport;

/**
 * Support for {@link Runnable} implementations.
 *
 * @since 1.0
 */
public abstract class RunnableSupport
    extends ComponentSupport
    implements Runnable
{
  public void run() {
    log.debug("Running");

    try {
      doRun();
    }
    catch (InterruptedException e) {
      log.warn("Interrupted", e);
      onFailure(e);
    }
    catch (Exception e) {
      log.error("Failed", e);
      onFailure(e);
    }

    log.debug("Stopped");
  }

  protected void onFailure(final Throwable cause) {
    // nop, logged above
  }

  protected abstract void doRun() throws Exception;
}