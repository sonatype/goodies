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
package org.sonatype.sisu.goodies.common;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SimpleFormat}.
 */
public class SimpleFormatTest
    extends TestSupport
{
  @Test
  public void testFormat() throws Exception {
    String value = SimpleFormat.format("foo %s", "bar");
    log(value);
    assertEquals("foo bar", value);
  }

  @Test
  public void invalidPlaceholder() throws Exception {
    String value = SimpleFormat.format("foo %i", "bar");
    log(value);
    assertEquals("foo %i [bar]", value);
  }

  @SuppressWarnings({"NullableProblems", "NullArgumentToVariableArgMethod"})
  @Test
  public void formatWithNullArgs() throws Exception {
    String value = SimpleFormat.format("foo %s", null);
    log(value);
    assertEquals("foo %s", value);
  }

  @SuppressWarnings("RedundantArrayCreation")
  @Test
  public void formatWithEmptyArgs() throws Exception {
    String value = SimpleFormat.format("foo %s", new Object[0]);
    log(value);
    assertEquals("foo %s", value);
  }

  @SuppressWarnings("NullableProblems")
  @Test
  public void formatWithNullTemplate() throws Exception {
    String value = SimpleFormat.format(null, "foo");
    log(value);
    assertEquals("null [foo]", value);
  }

  @Test
  public void templateUse() {
    FormatTemplate t = SimpleFormat.template("foo %s", "bar");
    assertEquals("foo %s", t.getFormat());
    assertEquals(1, t.getArgs().length);
    assertEquals("foo bar", t.evaluate());
  }

  @Test
  public void templateUseWithDynamic() {
    StringBuilder buff = new StringBuilder("bar");
    FormatTemplate t = SimpleFormat.template("foo %s", buff);
    t.setDynamic(true);
    assertEquals("foo %s", t.getFormat());
    assertEquals(1, t.getArgs().length);
    assertEquals("foo bar", t.evaluate());

    buff.append(" baz");
    assertEquals("foo bar baz", t.evaluate());
  }
}
