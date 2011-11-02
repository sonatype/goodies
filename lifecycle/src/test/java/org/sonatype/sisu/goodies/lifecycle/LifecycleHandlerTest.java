/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.goodies.lifecycle;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;
import statemap.TransitionUndefinedException;

import static org.junit.Assert.fail;

/**
 * Tests for {@link LifecycleHandler}.
 */
public class LifecycleHandlerTest
    extends TestSupport
{
    @Test
    public void simpleTest() {
        LifecycleHandler handler = new LifecycleHandler()
        {
            public Throwable getFailure() {
                return null;
            }

            public void doStart() {
                log("DO START");
            }

            public void doStop() {
                log("DO STOP");
            }
        };

        LifecycleHandlerContext context = new LifecycleHandlerContext(handler);

        log("state starts at " + context.getState());

        log("go start");
        context.start();
        log("state is now " + context.getState());

        log("go started");
        context.started();
        log("state is now " + context.getState());

        log("go stop");
        context.stop();
        log("state is now " + context.getState());

        log("go stopped");
        context.stopped();
        log("state is now " + context.getState());

        try {
            context.stop();
            fail();
        }
        catch (TransitionUndefinedException e) {
            // ignore
        }
    }
}