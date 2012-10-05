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

package org.sonatype.sisu.goodies.jmx;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import javax.management.ObjectName;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.sonatype.sisu.goodies.jmx.ObjectNameBuilder.STAR;

/**
 * Test for {@link ObjectNameBuilder}.
 */
public class ObjectNameBuilderTest
    extends TestSupport
{
    @Test(expected = IllegalStateException.class)
    public void missingDomainAndProperties() throws Exception {
        new ObjectNameBuilder().build();
    }

    @Test(expected = IllegalStateException.class)
    public void missingProperties() throws Exception {
        new ObjectNameBuilder().domain("a").build();
    }

    @Test(expected = IllegalStateException.class)
    public void missingDomain() throws Exception {
        new ObjectNameBuilder().property("a", "b").build();
    }

    @Test(expected = NullPointerException.class)
    public void domainNotNull() throws Exception {
        new ObjectNameBuilder()
            .domain(null)
            .property("b", "1")
            .build();
    }

    @Test
    public void singleProperty() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain("a")
            .property("b", "1")
            .build();
        log(name);

        assertThat(name.getDomain(), is("a"));
        assertThat(name.getKeyPropertyList().size(), is(1));
        assertThat(name.getKeyProperty("b"), is("1"));
    }

    @Test(expected = NullPointerException.class)
    public void propertyNameNotNull() throws Exception {
        new ObjectNameBuilder()
            .domain("a")
            .property(null, "1")
            .build();
    }


    @Test(expected = NullPointerException.class)
    public void propertyValueNotNull() throws Exception {
        new ObjectNameBuilder()
            .domain("a")
            .property("b", null)
            .build();
    }

    @Test
    public void singlePropertyFormat() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain("a")
            .property("b", "%s", 1)
            .build();
        log(name);

        assertThat(name.getDomain(), is("a"));
        assertThat(name.getKeyPropertyList().size(), is(1));
        assertThat(name.getKeyProperty("b"), is("1"));
    }

    @Test
    public void manyProperties() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain("a")
            .property("b", "1")
            .property("c", "2")
            .build();
        log(name);

        assertThat(name.getDomain(), is("a"));
        assertThat(name.getKeyPropertyList().size(), is(2));
        assertThat(name.getKeyProperty("b"), is("1"));
        assertThat(name.getKeyProperty("c"), is("2"));
    }

    @Test
    public void domainFormat() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain("%s", "a")
            .property("b", "1")
            .build();
        log(name);

        assertThat(name.getDomain(), is("a"));
    }

    @Test
    public void domainWildcard() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain()
            .property("b", "1")
            .build();
        log(name);

        assertThat(name.getDomain(), is(STAR));
        assertThat(name.getKeyPropertyList().size(), is(1));
        assertThat(name.getKeyProperty("b"), is("1"));
        assertThat(name.isPattern(), is(true));
        assertThat(name.isDomainPattern(), is(true));
        assertThat(name.isPropertyPattern(), is(false));
        assertThat(name.isPropertyListPattern(), is(false));
        assertThat(name.isPropertyValuePattern(), is(false));
    }

    @Test
    public void wildcardProperties() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain("a")
            .property("b", "1")
            .property()
            .build();
        log(name);

        assertThat(name.getDomain(), is("a"));
        assertThat(name.getKeyPropertyList().size(), is(1));
        assertThat(name.getKeyProperty("b"), is("1"));
        assertThat(name.isPattern(), is(true));
        assertThat(name.isDomainPattern(), is(false));
        assertThat(name.isPropertyPattern(), is(true));
        assertThat(name.isPropertyListPattern(), is(true));
        assertThat(name.isPropertyValuePattern(), is(false));
    }

    @Test
    public void wildcardValueProperties() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain("a")
            .property("b", "1")
            .property("c")
            .build();
        log(name);

        assertThat(name.getDomain(), is("a"));
        assertThat(name.getKeyPropertyList().size(), is(2));
        assertThat(name.getKeyProperty("b"), is("1"));
        assertThat(name.getKeyProperty("c"), is(STAR));
        assertThat(name.isPattern(), is(true));
        assertThat(name.isDomainPattern(), is(false));
        assertThat(name.isPropertyPattern(), is(true));
        assertThat(name.isPropertyListPattern(), is(false));
        assertThat(name.isPropertyValuePattern(), is(true));
    }

    @Test
    public void allWildcards() throws Exception {
        ObjectName name = new ObjectNameBuilder()
            .domain()
            .property("b", "1")
            .property("c")
            .property()
            .build();
        log(name);

        assertThat(name.getDomain(), is(STAR));
        assertThat(name.getKeyPropertyList().size(), is(2));
        assertThat(name.getKeyProperty("b"), is("1"));
        assertThat(name.getKeyProperty("c"), is(STAR));
        assertThat(name.isPattern(), is(true));
        assertThat(name.isDomainPattern(), is(true));
        assertThat(name.isPropertyPattern(), is(true));
        assertThat(name.isPropertyListPattern(), is(true));
        assertThat(name.isPropertyValuePattern(), is(true));
    }
}
