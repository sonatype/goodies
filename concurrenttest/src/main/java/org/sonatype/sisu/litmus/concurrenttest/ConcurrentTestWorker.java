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
package org.sonatype.sisu.litmus.concurrenttest;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.sonatype.sisu.goodies.common.ComponentSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A helper class to wrap a {@link ConcurrentTask} and trap any exceptions that it throws. If an exception is thrown,
 * it
 * trips a flag to force its peers to abort sooner, rather than waiting for the executor service to time out.
 */
class ConcurrentTestWorker
    extends ComponentSupport
    implements Callable<Void>
{
  private final ConcurrentTask concurrentTask;

  private final int workerNumber;

  private final AtomicReference<Throwable> exception = new AtomicReference<>();

  private final ConcurrentTestContext context;

  public ConcurrentTestWorker(final int workerNumber,
                              final ConcurrentTask concurrentTask,
                              final ConcurrentTestContext context)
  {
    this.workerNumber = workerNumber;
    this.context = checkNotNull(context);
    this.concurrentTask = checkNotNull(concurrentTask);
  }

  @Override
  public Void call() throws Exception {
      try {
      for (int i = 0; i < context.getIterations(); i++) {
        if (context.testFailing()) {
          log.info("Test worker " + workerNumber + " aborting before iteration " + i);
          break;
        }
        context.awaitIterationStart();
        context.recordRunInvocation();

        concurrentTask.run();
        log.info("Test worker " + workerNumber + " completing iteration " + i);
      }
    }
    catch (Exception | AssertionError e) {
      log.info("Test worker " + workerNumber + "'s task threw exception", e);
      context.indicateFailure();
      this.exception.set(e);
    }
    finally {
      log.info("Test worker " + workerNumber + " done.");
    }
    return null;
  }

  public boolean experiencedFailure() {
    return exception.get() != null;
  }

  public Throwable getException() {
    return exception.get();
  }
}
