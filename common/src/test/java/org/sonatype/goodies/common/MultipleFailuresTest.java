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
package org.sonatype.goodies.common;

import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link MultipleFailures}.
 */
public class MultipleFailuresTest
    extends TestSupport
{
  private MultipleFailures underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new MultipleFailures();
  }

  @Test
  public void propagate_with_no_failures() throws Exception {
    assertTrue(underTest.getFailures().isEmpty());
    underTest.maybePropagate();
  }

  @Test
  public void propagate_single()throws Exception {
    underTest.add(new Exception("TEST"));
    try {
      underTest.maybePropagate("OOPS");
      fail();
    }
    catch (Exception e) {
      Throwable[] suppressed = e.getSuppressed();
      assertThat(suppressed.length, is(1));
      assertThat(suppressed[0].getMessage(), is("TEST"));
    }
  }

  @Test
  public void propagate_multi() throws Exception {
    underTest.add(new Exception("FOO"));
    underTest.add(new Exception("BAR"));
    try {
      underTest.maybePropagate("OOPS");
      fail();
    }
    catch (Exception e) {
      Throwable[] suppressed = e.getSuppressed();
      assertThat(suppressed.length, is(2));
      assertThat(suppressed[0].getMessage(), is("FOO"));
      assertThat(suppressed[1].getMessage(), is("BAR"));
    }
  }
}
