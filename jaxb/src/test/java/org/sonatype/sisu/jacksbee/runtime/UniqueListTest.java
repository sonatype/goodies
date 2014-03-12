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

import java.util.List;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link UniqueList}.
 */
public class UniqueListTest
    extends TestSupport
{
  @Test
  public void testAddDuplicates() {
    UniqueList<String> list = UniqueList.create();
    list.add("foo");
    list.add("bar");
    assertEquals(2, list.size());

    list.add("bar");
    assertEquals(2, list.size());
  }

  @Test
  public void testRemove() {
    UniqueList<String> list = UniqueList.create();
    list.add("foo");
    list.add("bar");
    assertEquals(2, list.size());

    list.remove("bar");
    assertEquals(1, list.size());
  }

  @Test
  public void testSerializationViaXStream() throws Exception {
    XStream xs = new XStream();
    xs.processAnnotations(UniqueList.class);

    UniqueList<String> list1 = UniqueList.create();
    list1.add("foo");
    list1.add("bar");

    String xml = xs.toXML(list1);

    @SuppressWarnings({"unchecked"})
    UniqueList<String> list2 = (UniqueList<String>) xs.fromXML(xml);
    assertEquals(list1, list2);
  }

  @Test
  public void testSerializationViaXStreamWithManualDuplicates() throws Exception {
    XStream xs = new XStream();
    xs.processAnnotations(UniqueList.class);

    String xml = "<unique-list>\n" +
        "  <string>foo</string>\n" +
        "  <string>foo</string>\n" +
        "  <string>foo</string>\n" +
        "  <string>bar</string>\n" +
        "  <string>bar</string>\n" +
        "</unique-list>";

    @SuppressWarnings({"unchecked"})
    UniqueList<String> list = (UniqueList<String>) xs.fromXML(xml);
    assertEquals(2, list.size());
    assertEquals("foo", list.get(0));
    assertEquals("bar", list.get(1));
  }

  @Test
  public void hasSameHashcode() {
    List<String> list1 = Lists.newArrayList("a", "b", "c");
    UniqueList<String> list2 = UniqueList.create(list1);
    assertEquals(list1.hashCode(), list2.hashCode());
  }

  @Test
  public void hasSameToString() {
    List<String> list1 = Lists.newArrayList("a", "b", "c");
    UniqueList<String> list2 = UniqueList.create(list1);
    assertEquals(list1.toString(), list2.toString());
  }
}
