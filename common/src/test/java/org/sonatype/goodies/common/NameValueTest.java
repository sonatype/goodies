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

import java.util.Map;

import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link NameValue}.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class NameValueTest
    extends TestSupport
{
  @Test
  public void parse() {
    NameValue nv = NameValue.parse("a=b");
    assertThat(nv.name, equalTo("a"));
    assertThat(nv.value, equalTo("b"));
  }

  @Test
  public void parseNoValue() {
    NameValue nv = NameValue.parse("a");
    assertThat(nv.name, equalTo("a"));
    assertThat(nv.value, equalTo(NameValue.TRUE));
  }

  @Test
  public void decodeWithComma() {
    String input = "foo=bar,baz=ick";
    Map<String, String> have = NameValue.decode(input);
    assertEquals(2, have.size());
    assertEquals("bar", have.get("foo"));
    assertEquals("ick", have.get("baz"));
  }

  @Test
  public void decodeWithSlash() {
    String input = "foo=bar/baz=ick";
    Map<String, String> have = NameValue.decode(input);
    assertEquals(2, have.size());
    assertEquals("bar", have.get("foo"));
    assertEquals("ick", have.get("baz"));
  }

  @Test
  public void decodeWithSpacesAndValueTrim() {
    String input = "foo=bar   ,\n   baz=ick";
    Map<String, String> have = NameValue.decode(input, true);
    assertEquals(2, have.size());
    assertEquals("bar", have.get("foo"));
    assertEquals("ick", have.get("baz"));
  }

  @Test
  public void decodeWithSpacesAndNoValueTrim() {
    String input = "foo=bar   ,\n   baz=ick";
    Map<String, String> have = NameValue.decode(input, false);
    assertEquals(2, have.size());
    assertEquals("bar   ", have.get("foo"));
    assertEquals("ick", have.get("baz"));
  }
}
