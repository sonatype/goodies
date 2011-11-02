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

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link Time}.
 */
public class TimeTest
    extends TestSupport
{
    @Test(expected = NullPointerException.class)
    public void nullUnit() {
        new Time(1, null);
    }

    @Test
    public void testTimeConversions() {
        Time oneDay = Time.days(1);

        assertEquals(1 * 24 * 60 * 60 * 1000 * 1000L, oneDay.toMicros());
        assertEquals(1 * 24 * 60 * 60 * 1000L, oneDay.toMillis());
        assertEquals(1 * 24 * 60 * 60L, oneDay.toSeconds());
        assertEquals(1 * 24 * 60L, oneDay.toMinutes());
        assertEquals(1 * 24L, oneDay.toHours());
        assertEquals(1L, oneDay.toDays());

        assertEquals(Time.micros(1 * 24 * 60 * 60 * 1000 * 1000L).toDays(), oneDay.getValue());
        assertEquals(Time.millis(1 * 24 * 60 * 60 * 1000L).toDays(), oneDay.getValue());
        assertEquals(Time.seconds(1 * 24 * 60 * 60L).toDays(), oneDay.getValue());
        assertEquals(Time.minutes(1 * 24 * 60L).toDays(), oneDay.getValue());
        assertEquals(Time.hours(1 * 24L).toDays(), oneDay.getValue());
        assertEquals(Time.days(1L).toDays(), oneDay.getValue());
    }
}