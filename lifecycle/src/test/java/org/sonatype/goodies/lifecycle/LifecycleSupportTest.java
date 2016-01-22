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
package org.sonatype.goodies.lifecycle;

import org.sonatype.goodies.lifecycle.LifecycleSupport.State;
import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link LifecycleSupport}.
 */
public class LifecycleSupportTest
    extends TestSupport
{
  private static void assertState(final LifecycleSupport lifecycle, final State state) {
    assertTrue(lifecycle.is(state));
  }

  @Test
  public void startStopStartStop() throws Exception {
    LifecycleSupport underTest = new LifecycleSupport();

    assertState(underTest, State.NEW);

    underTest.start();
    assertState(underTest, State.STARTED);

    underTest.stop();
    assertState(underTest, State.STOPPED);

    underTest.start();
    assertState(underTest, State.STARTED);

    underTest.stop();
    assertState(underTest, State.STOPPED);
  }

  @Test
  public void stopBeforeStartDisallowed() throws Exception {
    LifecycleSupport underTest = new LifecycleSupport();

    assertState(underTest, State.NEW);

    try {
      underTest.stop();
    }
    catch (IllegalStateException e) {
      // expected
    }

    assertState(underTest, State.NEW);
  }

  @Test
  public void startException() throws Exception {
    LifecycleSupport underTest = new LifecycleSupport()
    {
      @Override
      protected void doStart() throws Exception {
        throw new TestException();
      }
    };

    try {
      underTest.start();
      fail();
    }
    catch (TestException e) {
      // expected
    }

    assertState(underTest, State.FAILED);
  }

  @Test
  public void startError() throws Exception {
    LifecycleSupport underTest = new LifecycleSupport()
    {
      @Override
      protected void doStart() throws Exception {
        throw new TestError();
      }
    };

    try {
      underTest.start();
      fail();
    }
    catch (TestError e) {
      // expected
    }

    assertState(underTest, State.FAILED);
  }

  @Test
  public void stopException() throws Exception {
    LifecycleSupport underTest = new LifecycleSupport()
    {
      @Override
      protected void doStop() throws Exception {
        throw new TestException();
      }
    };

    underTest.start();
    try {
      underTest.stop();
      fail();
    }
    catch (TestException e) {
      // expected
    }

    assertState(underTest, State.FAILED);
  }

  @Test
  public void stopError() throws Exception {
    LifecycleSupport underTest = new LifecycleSupport()
    {
      @Override
      protected void doStop() throws Exception {
        throw new TestError();
      }
    };

    underTest.start();
    try {
      underTest.stop();
      fail();
    }
    catch (TestError e) {
      // expected
    }

    assertState(underTest, State.FAILED);
  }
}