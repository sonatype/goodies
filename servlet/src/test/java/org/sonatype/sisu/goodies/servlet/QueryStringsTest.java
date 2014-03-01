/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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

import java.util.Iterator;
import java.util.Map.Entry;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.Multimap;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
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
    Multimap<String, String> parsed = QueryStrings.parse("a=1");
    log(parsed);

    assertNotNull(parsed);
    assertThat(parsed.size(), is(1));
    assertThat(parsed.get("a").size(), is(1));
    assertThat(parsed.get("a"), contains("1"));
  }

  @Test
  public void parseDuplicateValues() throws Exception {
    Multimap<String, String> parsed = QueryStrings.parse("a=1&a=1&a=1");
    log(parsed);

    assertNotNull(parsed);
    assertThat(parsed.size(), is(1));
    assertThat(parsed.get("a").size(), is(1));
    assertThat(parsed.get("a"), contains("1"));
  }

  @Test
  public void parseMultiple() throws Exception {
    Multimap<String, String> parsed = QueryStrings.parse("a=1&b=2");
    log(parsed);

    assertNotNull(parsed);
    assertThat(parsed.size(), is(2));
    assertThat(parsed.get("a").size(), is(1));
    assertThat(parsed.get("a"), contains("1"));
    assertThat(parsed.get("b").size(), is(1));
    assertThat(parsed.get("b"), contains("2"));
  }

  @Test
  public void parseSingleNoValue() throws Exception {
    Multimap<String, String> parsed = QueryStrings.parse("a");
    log(parsed);

    assertNotNull(parsed);
    assertThat(parsed.size(), is(1));
    assertThat(parsed.get("a").size(), is(1));
    assertThat(parsed.get("a").iterator().next(), is((String) null));
  }

  @Test
  public void parseOrder() {
    Multimap<String, String> parsed = QueryStrings.parse("a=1&b=2");
    log(parsed);

    assertNotNull(parsed);
    assertThat(parsed.size(), is(2));
    Iterator<Entry<String, String>> iter = parsed.entries().iterator();
    assertThat(iter.next().getKey(), is("a"));
    assertThat(iter.next().getKey(), is("b"));
  }

  @Test
  public void parseOrderMultipuleValues() {
    Multimap<String, String> parsed = QueryStrings.parse("a=1&b=2&a=3&b=4");
    log(parsed);

    assertNotNull(parsed);
    assertThat(parsed.size(), is(4));
    Iterator<Entry<String, String>> iter = parsed.entries().iterator();
    Entry<String, String> entry;

    entry = iter.next();
    log(entry);
    assertThat(entry.getKey(), is("a"));
    assertThat(entry.getValue(), is("1"));

    entry = iter.next();
    log(entry);
    assertThat(entry.getKey(), is("b"));
    assertThat(entry.getValue(), is("2"));

    entry = iter.next();
    log(entry);
    assertThat(entry.getKey(), is("a"));
    assertThat(entry.getValue(), is("3"));

    entry = iter.next();
    log(entry);
    assertThat(entry.getKey(), is("b"));
    assertThat(entry.getValue(), is("4"));
  }
}