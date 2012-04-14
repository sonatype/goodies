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

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
