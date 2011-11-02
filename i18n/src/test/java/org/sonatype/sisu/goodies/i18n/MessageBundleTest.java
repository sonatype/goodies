/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.i18n;

import org.junit.Before;
import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

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