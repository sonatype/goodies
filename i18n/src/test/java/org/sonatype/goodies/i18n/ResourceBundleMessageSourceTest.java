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
package org.sonatype.goodies.i18n;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ResourceBundleMessageSource}.
 */
public class ResourceBundleMessageSourceTest
    extends TestSupport
{
  private MessageSource messages;

  @Before
  public void setUp() {
    messages = new ResourceBundleMessageSource(getClass());
  }

  @Test
  public void testLoadAndGetMessage() {
    String a = messages.getMessage("a");
    assertEquals("1", a);

    String b = messages.getMessage("b");
    assertEquals("2", b);

    String c = messages.getMessage("c");
    assertEquals("3", c);

    String f = messages.format("f", a, b, c);
    assertEquals("1 2 3", f);
  }

  @Test
  public void testMissingResource() throws Exception {
    try {
      messages.getMessage("no-such-code");
    }
    catch (ResourceNotFoundException e) {
      // ignore
    }
  }

  @Test
  public void testMissingResourceWithDefault() throws Exception {
    String msg = messages.getMessage("no-such-code", "foo");
    assertEquals("foo", msg);
  }
}