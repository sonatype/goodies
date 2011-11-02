/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link Iso8601Date}.
 */
public class Iso8601DateTest
    extends TestSupport
{
    @Test
    public void testFormatParse() throws Exception {
        Date date1 = new Date();

        String formatted = Iso8601Date.format(date1);
        assertNotNull(formatted);

        Date date2 = Iso8601Date.parse(formatted);
        assertNotNull(date2);

        assertEquals(date1.getTime(), date2.getTime());
    }
}
