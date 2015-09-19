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
package org.sonatype.goodies.i18n;

import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link MessageBundle} use.
 */
public class MessageBundleTest
    extends TestSupport
{
  public static interface Messages
      extends MessageBundle
  {
    @DefaultMessage("This is a test")
    String test1();

    @DefaultMessage("s:%s,i:%s")
    String testWithFormat(String a, int b);

    String testMissing();

    Object testInvalid();
  }

  private Messages messages;

  @Before
  public void setUp() throws Exception {
    messages = I18N.create(Messages.class);
    assertNotNull(messages);
  }

  @Test
  public void testDefaultMessage() {
    String msg = messages.test1();
    assertEquals("This is a test", msg);
  }

  @Test
  public void testDefaultMessageWithFormat() {
    String msg = messages.testWithFormat("foo", 1);
    assertEquals("s:foo,i:1", msg);
  }

  @Test
  public void testMissing() {
    String msg = messages.testMissing();
    assertEquals(String.format(I18N.MISSING_MESSAGE_FORMAT, "testMissing"), msg);
  }

  @Test(expected = Error.class)
  public void testInvalid() {
    messages.testInvalid();
  }
}