/*
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link NameValue}.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class NameValueTest
    extends TestSupport
{
    @Test
    public void parse() {
        NameValue nv = NameValue.parse("a=b");
        assertThat(nv.name, equalTo("a"));
        assertThat(nv.value, equalTo("b"));
    }

    @Test
    public void parseNoValue() {
        NameValue nv = NameValue.parse("a");
        assertThat(nv.name, equalTo("a"));
        assertThat(nv.value, equalTo(NameValue.TRUE));
    }

    @Test
    public void decodeWithComma() {
        String input = "foo=bar,baz=ick";
        Map<String,String> have = NameValue.decode(input);
        assertEquals(2, have.size());
        assertEquals("bar", have.get("foo"));
        assertEquals("ick", have.get("baz"));
    }

    @Test
    public void decodeWithSlash() {
        String input = "foo=bar/baz=ick";
        Map<String,String> have = NameValue.decode(input);
        assertEquals(2, have.size());
        assertEquals("bar", have.get("foo"));
        assertEquals("ick", have.get("baz"));
    }
}
