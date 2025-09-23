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
package org.sonatype.goodies.lifecycle;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.sonatype.goodies.common.MultipleFailures.MultipleFailuresException;
import org.sonatype.goodies.lifecycle.LifecycleSupport.State;
import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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

  @Test
  public void startStopOrdering() throws Exception {
    final List<LifecycleSupport> started = new ArrayList<>();
    final List<LifecycleSupport> stopped = new ArrayList<>();

    LifecycleManager underTest = new LifecycleManager();
    LifecycleSupport foo = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        started.add(this);
      }

      @Override
      protected void doStop() throws Exception {
        stopped.add(this);
      }
    };
    Lifecycle bar = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        started.add(this);
      }

      @Override
      protected void doStop() throws Exception {
        stopped.add(this);
      }
    };

    underTest.add(foo, bar);

    assertThat(started.size(), is(0));
    assertThat(stopped.size(), is(0));

    underTest.start();
    assertThat(started.size(), is(2));
    assertThat(started, contains(foo, bar));

    underTest.stop();
    assertThat(stopped.size(), is(2));
    assertThat(stopped, contains(bar, foo));
  }

  @Test
  public void startWithSingleFailure() throws Exception {
    LifecycleManager underTest = new LifecycleManager();
    LifecycleSupport foo = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        throw new TestException();
      }
    };
    LifecycleSupport bar = new LifecycleSupport();

    underTest.add(foo, bar);

    try {
      underTest.start();
    }
    catch (MultipleFailuresException e) {
      List<Throwable> failures = e.getFailures();
      assertThat(failures.size(), is(1));
      assertThat(failures.get(0), instanceOf(TestException.class));

      assertState(foo, State.FAILED);
      assertState(bar, State.STARTED);
    }
  }

  @Test
  public void startWithMultipleFailures() throws Exception {
    LifecycleManager underTest = new LifecycleManager();
    LifecycleSupport foo = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        throw new TestException();
      }
    };
    LifecycleSupport bar = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        throw new TestError();
      }
    };

    underTest.add(foo, bar);

    try {
      underTest.start();
    }
    catch (MultipleFailuresException e) {
      List<Throwable> failures = e.getFailures();
      assertThat(failures.size(), is(2));
      assertThat(failures.get(0), instanceOf(TestException.class));
      assertThat(failures.get(1), instanceOf(TestError.class));

      assertState(foo, State.FAILED);
      assertState(bar, State.FAILED);
    }
  }

  @Test
  public void stopWithSingleFailure() throws Exception {
    LifecycleManager underTest = new LifecycleManager();
    LifecycleSupport foo = new LifecycleSupport() {
      @Override
      protected void doStop() throws Exception {
        throw new TestException();
      }
    };
    LifecycleSupport bar = new LifecycleSupport();

    underTest.add(foo, bar);

    underTest.start();

    try {
      underTest.stop();
    }
    catch (MultipleFailuresException e) {
      List<Throwable> failures = e.getFailures();
      assertThat(failures.size(), is(1));
      assertThat(failures.get(0), instanceOf(TestException.class));

      assertState(foo, State.FAILED);
      assertState(bar, State.STOPPED);
    }
  }

  @Test
  public void stopWithMultipleFailures() throws Exception {
    LifecycleManager underTest = new LifecycleManager();
    LifecycleSupport foo = new LifecycleSupport() {
      @Override
      protected void doStop() throws Exception {
        throw new TestException();
      }
    };
    LifecycleSupport bar = new LifecycleSupport() {
      @Override
      protected void doStop() throws Exception {
        throw new TestError();
      }
    };

    underTest.add(foo, bar);

    underTest.start();

    try {
      underTest.stop();
    }
    catch (MultipleFailuresException e) {
      List<Throwable> failures = e.getFailures();
      assertThat(failures.size(), is(2));
      assertThat(failures.get(0), instanceOf(TestError.class));
      assertThat(failures.get(1), instanceOf(TestException.class));

      assertState(foo, State.FAILED);
      assertState(bar, State.FAILED);
    }
  }

  @Test
  public void awareStartStopOrdering() throws Exception {
    final List<Lifecycle> started = new ArrayList<>();
    final List<Lifecycle> stopped = new ArrayList<>();

    LifecycleManager underTest = new LifecycleManager();
    final Lifecycle foo = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        started.add(this);
      }

      @Override
      protected void doStop() throws Exception {
        stopped.add(this);
      }
    };
    final Lifecycle bar = new LifecycleSupport() {
      @Override
      protected void doStart() throws Exception {
        started.add(this);
      }

      @Override
      protected void doStop() throws Exception {
        stopped.add(this);
      }
    };

    underTest.add(
        new LifecycleAware()
        {
          @Nonnull
          @Override
          public Lifecycle getLifecycle() {
            return foo;
          }
        },
        new LifecycleAware() {
          @Nonnull
          @Override
          public Lifecycle getLifecycle() {
            return bar;
          }
        }
    );

    assertThat(started.size(), is(0));
    assertThat(stopped.size(), is(0));

    underTest.start();
    assertThat(started.size(), is(2));
    assertThat(started, contains(foo, bar));

    underTest.stop();
    assertThat(stopped.size(), is(2));
    assertThat(stopped, contains(bar, foo));
  }

}