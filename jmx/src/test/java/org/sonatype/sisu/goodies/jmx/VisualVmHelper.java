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
package org.sonatype.sisu.goodies.jmx;

import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VisualVM helper.
 *
 * Set {@code -Dvisualvm.display.name=<name>} (no spaces) as JVM arguments to get VisualVM to display a better name for
 * the application.
 */
public class VisualVmHelper
{
  private static final Logger log = LoggerFactory.getLogger(VisualVmHelper.class);

  public static int getProcessId() {
    final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
    final int index = jvmName.indexOf('@');
    if (index < 1) {
      throw new RuntimeException("Unable to parse PID from: " + jvmName);
    }
    return Integer.parseInt(jvmName.substring(0, index));
  }

  public static Process openPid(final int pid) throws Exception {
    log.info("Opening PID: {}", pid);

    final Process proc = Runtime.getRuntime().exec("jvisualvm --openpid " + pid);
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run() {
        proc.destroy();
      }
    });
    return proc;
  }

  public static Process openCurrentPid() throws Exception {
    return openPid(getProcessId());
  }
}
