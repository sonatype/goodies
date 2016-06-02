/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.testsupport.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Context shared between the {@link ConcurrentRunner} and the various {@link ConcurrentTestWorker}s.
 *
 * @since 1.15
 */
class ConcurrentTestContext
{
  private final CyclicBarrier iterationStartSignal;

  private final AtomicBoolean failureSignal;

  private final int iterations;

  private final AtomicInteger runInvocations;

  private final int iterationStartTimeoutSeconds;

  public ConcurrentTestContext(final CyclicBarrier iterationStartSignal,
                               final AtomicBoolean failureSignal,
                               final int iterations,
                               final AtomicInteger runInvocations,
                               final int iterationStartTimeoutSeconds)
  {
    this.iterationStartTimeoutSeconds = iterationStartTimeoutSeconds;
    this.iterationStartSignal = checkNotNull(iterationStartSignal);
    this.failureSignal = checkNotNull(failureSignal);
    this.iterations = iterations;
    this.runInvocations = checkNotNull(runInvocations);
  }

  public void recordRunInvocation() {
    runInvocations.incrementAndGet();
  }

  public int getIterations() {
    return iterations;
  }

  public boolean testFailing() {
    return failureSignal.get();
  }

  public void indicateFailure() {
    failureSignal.set(true);
    // Dislodge any peers waiting at the barrier; the test has failed, so don't wait for timeout
    // interrupting ourselves and attempting to join the barrier will permanently break it, unlike reset() which will
    // only break the current generation and then start a new generation on which other threads could again wait on
    // until they timeout if they weren't already waiting at the time reset() gets called
    Thread.currentThread().interrupt();
    try {
      iterationStartSignal.await(0, TimeUnit.SECONDS);
    }
    catch (InterruptedException | BrokenBarrierException | TimeoutException ignored) { // NOSONAR
      // we wanted to break the barrier, well we did it
    }
  }

  public void awaitIterationStart() throws Exception {
    iterationStartSignal.await(iterationStartTimeoutSeconds, TimeUnit.SECONDS);
  }
}
