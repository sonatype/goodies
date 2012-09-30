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

package org.sonatype.sisu.goodies.servlet;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link QueryStrings}
 */
public class QueryStringsTest
    extends TestSupport
{
    @Test
    public void parseSingle() throws Exception {
        Map<String, String> parsed = QueryStrings.parse("a=1");
        log(parsed);

        assertNotNull(parsed);
        assertThat(parsed.size(), is(1));
        assertThat(parsed.get("a"), is("1"));
    }

    @Test
    public void parseMultiple() throws Exception {
        Map<String, String> parsed = QueryStrings.parse("a=1&b=2");
        log(parsed);

        assertNotNull(parsed);
        assertThat(parsed.size(), is(2));
        assertThat(parsed.get("a"), is("1"));
        assertThat(parsed.get("b"), is("2"));
    }

    @Test
    public void parseSingleNoValue() throws Exception {
        Map<String, String> parsed = QueryStrings.parse("a");
        log(parsed);

        assertNotNull(parsed);
        assertThat(parsed.size(), is(1));
        assertThat(parsed.get("a"), is((String) null));
    }

    @Test
    public void parseOrder() {
        Map<String, String> parsed = QueryStrings.parse("a=1&b=2");
        log(parsed);

        assertNotNull(parsed);
        assertThat(parsed.size(), is(2));
        Iterator<Entry<String, String>> iter = parsed.entrySet().iterator();
        assertThat(iter.next().getKey(), is("a"));
        assertThat(iter.next().getKey(), is("b"));
    }
}