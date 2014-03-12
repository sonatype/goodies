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

package org.sonatype.sisu.jacksbee.runtime;

import java.util.Date;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

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
