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

import org.junit.Test;
import org.sonatype.sisu.goodies.common.Mutex;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link ThreadSupport}.
 */
public class ThreadSupportTest
    extends TestSupport
{
    @Test
    public void testCancelTasks()
        throws InterruptedException
    {
        final AtomicBoolean canceled = new AtomicBoolean(false);
        ThreadSupport ts = new ThreadSupport()
        {
            @Override
            protected void doRun() throws Exception {
                int i = 0;
                while (!isCanceled() && i++ <= 20) {
                    Thread.sleep(50);
                }
                canceled.set(isCanceled());
            }
        };

        ts.start();
        ts.cancel();
        Thread.sleep(100);

        assertThat(canceled.get(), equalTo(true));
    }

    @Test
    public void testLockedCancel()
        throws InterruptedException
    {
        final AtomicBoolean canceled = new AtomicBoolean(false);
        ThreadSupport ts = new ThreadSupport()
        {
            @Override
            protected void doRun() throws Exception {
                Mutex l = getLock();
                synchronized (l) {
                    l.wait(1000);
                }
                canceled.set(isCanceled());
            }
        };

        ts.start();

        // wait for ts to get lock
        Thread.sleep(200);

        ts.cancel();

        Thread.sleep(100);

        assertThat(canceled.get(), equalTo(true));
    }

    @Test
    public void testFailure() {
        final AtomicBoolean failure = new AtomicBoolean(false);
        ThreadSupport ts = new ThreadSupport()
        {
            @Override
            protected void doRun() throws Exception {
                throw new Exception();
            }

            @Override
            protected void onFailure(Throwable cause) {
                failure.set(true);
            }
        };

        ts.run();

        assertThat(failure.get(), equalTo(true));
    }

    @Test
    public void testFailureOnStop() {
        final AtomicBoolean failure = new AtomicBoolean(false);
        ThreadSupport ts = new ThreadSupport()
        {
            @Override
            protected void doRun() throws Exception {
                // ignore
            }

            @Override
            protected void doStop() throws Exception {
                throw new Exception();
            }

            @Override
            protected void onFailure(Throwable cause) {
                failure.set(true);
            }
        };

        ts.run();

        assertThat(failure.get(), equalTo(true));
    }
}
