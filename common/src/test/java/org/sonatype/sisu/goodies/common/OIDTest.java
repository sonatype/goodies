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

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
        assertThat(OID.render(obj), is(equalTo(obj.toString())));
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
