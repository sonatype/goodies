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