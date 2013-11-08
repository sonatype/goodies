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

package org.sonatype.sisu.goodies.common;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link Throwables2}.
 */
public class Throwables2Test
    extends TestSupport
{
  @Test
  public void explainThrowable() {
    String msg = Throwables2.explain(new RuntimeException("foo"));
    log(msg);
    assertThat(msg, is("java.lang.RuntimeException: foo"));
  }

  @Test
  public void explainThrowable2() {
    String msg = Throwables2.explain(
        new RuntimeException("foo",
            new Exception("bar")
        )
    );
    log(msg);
    assertThat(msg, is("java.lang.RuntimeException: foo, caused by: java.lang.Exception: bar"));
  }

  @Test
  public void explainThrowable3() {
    String msg = Throwables2.explain(
        new RuntimeException("foo",
            new Exception(
                new Exception("bar")
            )
        )
    );
    log(msg);
    assertThat(msg, is("java.lang.RuntimeException: foo, caused by: java.lang.Exception, caused by: java.lang.Exception: bar"));
  }

  @Test
  public void explainThrowable4() {
    String msg = Throwables2.explain(
        new RuntimeException("foo",
            new Exception("bar",
                new Exception("baz")
            )
        )
    );
    log(msg);
    assertThat(msg, is("java.lang.RuntimeException: foo, caused by: java.lang.Exception: bar, caused by: java.lang.Exception: baz"));
  }
}
