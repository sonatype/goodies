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
package org.sonatype.sisu.litmus.testsupport.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.concurrent.ConcurrentRunner;
import org.sonatype.sisu.litmus.testsupport.concurrent.ConcurrentTask;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the {@link ConcurrentRunner}
 */
public class ConcurrentRunnerTest
    extends TestSupport
{
  @Test
  public void tasksAreRunRepeatedly() throws Exception {
    final int iterations = 5;
    final ConcurrentRunner runner = new ConcurrentRunner(iterations, 10);

    final AtomicInteger runCount = new AtomicInteger(0);

    final int threads = 2;
    runner.addTask(threads, new ConcurrentTask()
    {
      @Override
      public void run() throws Exception {
        runCount.incrementAndGet();
      }
    });

    runner.go();

    final int expectedNumberOfRunInvocations = threads * iterations;
    assertThat(runCount.intValue(), is(expectedNumberOfRunInvocations));
    assertThat(runner.getRunInvocations(), is(expectedNumberOfRunInvocations));
  }

  @Test(expected = IllegalStateException.class)
  public void taskExceptionPropagatesOut() throws Exception {
    final ConcurrentRunner runner = new ConcurrentRunner(1, 10);

    runner.addTask(new ConcurrentTask()
    {
      @Override
      public void run() throws Exception {
        throw new IllegalStateException();
      }
    });

    runner.go();
  }

  @Test(expected = IllegalStateException.class)
  public void testTimesOut() throws Exception {
    // The test will time out unless the workers all end in less than 1 second
    final ConcurrentRunner runner = new ConcurrentRunner(1, 1);
    runner.addTask(1, new ConcurrentTask()
    {
      @Override
      public void run() throws Exception {
        // Sleep for 50 seconds, way past the timeout
        Thread.sleep(50 * 1000);
      }
    });

    runner.go();
  }
}
