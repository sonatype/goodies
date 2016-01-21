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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link LifecycleManager}.
 */
public class LifecycleManagerTest
    extends TestSupport
{
  private static void assertState(final LifecycleSupport lifecycle, final State state) {
    assertTrue(lifecycle.is(state));
  }

  @Test
  public void addStartStopRemove() throws Exception {
    LifecycleManager underTest = new LifecycleManager();
    LifecycleSupport foo = new LifecycleSupport();
    LifecycleSupport bar = new LifecycleSupport();

    assertState(underTest, State.NEW);
    assertState(foo, State.NEW);
    assertState(bar, State.NEW);

    underTest.add(foo);
    assertThat(underTest.size(), is(1));

    underTest.add(bar);
    assertThat(underTest.size(), is(2));

    underTest.start();
    assertState(underTest, State.STARTED);
    assertState(foo, State.STARTED);
    assertState(bar, State.STARTED);

    underTest.stop();
    assertState(underTest, State.STOPPED);
    assertState(foo, State.STOPPED);
    assertState(bar, State.STOPPED);

    underTest.remove(foo);
    assertThat(underTest.size(), is(1));

    underTest.remove(bar);
    assertThat(underTest.size(), is(0));
  }

  // TODO: Add test with components which fail transitions to verify behavior

  // TODD: Add test to verify start and stop order

  // TODO: Add tests with LifecycleAware
}