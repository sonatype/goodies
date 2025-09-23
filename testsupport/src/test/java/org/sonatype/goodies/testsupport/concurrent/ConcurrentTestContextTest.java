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
package org.sonatype.goodies.testsupport.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Tests {@link ConcurrentTestContext}.
 */
public class ConcurrentTestContextTest
{
  @Test(expected = BrokenBarrierException.class)
  public void testAwaitIterationStart_FailFastWhenFailureOccurredAlready() throws Exception {
    ConcurrentTestContext context = new ConcurrentTestContext(new CyclicBarrier(2), new AtomicBoolean(), 2,
        new AtomicInteger(), 3);
    context.indicateFailure();
    context.awaitIterationStart();
  }
}
