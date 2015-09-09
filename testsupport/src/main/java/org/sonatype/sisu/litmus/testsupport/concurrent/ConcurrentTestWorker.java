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
package org.sonatype.sisu.litmus.testsupport.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A helper class to wrap a {@link ConcurrentTask} and trap any exceptions that it throws. If an exception is thrown,
 * it
 * trips a flag to force its peers to abort sooner, rather than waiting for the executor service to time out.
 *
 * @since 1.15
 */
class ConcurrentTestWorker
    implements Callable<Void>
{
  private final ConcurrentTask concurrentTask;

  private final int workerNumber;

  private final AtomicReference<Throwable> exception = new AtomicReference<>();

  private final ConcurrentTestContext context;

  private static final Logger log = LoggerFactory.getLogger(ConcurrentTestWorker.class);

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
          log.info("Test worker {} aborting before iteration {}", workerNumber, i);
          break;
        }
        context.awaitIterationStart();
        context.recordRunInvocation();

        concurrentTask.run();
        log.info("Test worker {} completing iteration {}", workerNumber, i);
      }
    }
    catch (Exception | AssertionError e) {
      log.info("Test worker {}'s task threw exception", workerNumber, e);
      context.indicateFailure();
      this.exception.set(e);
    }
    finally {
      log.info("Test worker {} done", workerNumber);
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
