/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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

import java.util.Arrays;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link OID}.
 */
public class OIDTest
    extends TestSupport
{
  @Test
  public void testSimple() {
    Object obj = new Object();
    OID oid = OID.get(obj);
    assertEquals(obj.toString(), oid.toString());
  }

  @Test
  public void testParse() {
    Object obj = new Object();
    String spec = obj.toString();
    OID oid = OID.parse(spec);
    assertEquals(obj.getClass().getName(), oid.getType());
    assertEquals(obj.hashCode(), oid.getHash());
    assertEquals(spec, oid.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseIllegalOID() {
    Object obj = new Object();
    OID.parse(obj.toString() + "@illegal");
  }

  @Test
  public void testGetNull() {
    OID oid = OID.get(null);
    assertEquals(OID.NULL, oid);
  }

  @Test
  public void testRender() {
    Object obj = new Object();
    String repr = OID.render(obj);
    log(repr);
    assertThat(repr, is(equalTo(obj.toString())));
    String prefix = Object.class.getName() + OID.SEPARATOR;
    assertThat(repr.startsWith(prefix), is(true));
    assertThat(repr.length(), greaterThan(prefix.length()));
  }

  @Test
  public void testFindById() {
    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    assertThat(OID.find(Arrays.asList(obj1, obj2, obj3), OID.render(obj2)), is(equalTo(obj2)));
  }

  @Test
  public void testFindByOID() {
    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    assertThat(OID.find(Arrays.asList(obj1, obj2, obj3), OID.oid(obj2)), is(equalTo(obj2)));
  }
}
