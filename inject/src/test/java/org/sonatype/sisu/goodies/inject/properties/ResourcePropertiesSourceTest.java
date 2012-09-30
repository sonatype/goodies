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

package org.sonatype.sisu.goodies.inject.properties;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ResourcePropertiesSource}.
 */
public class ResourcePropertiesSourceTest
    extends TestSupport
{
    @Test
    public void loadExisting() {
        ResourcePropertiesSource source = new ResourcePropertiesSource(getClass(), "test1.properties");
        Properties props = source.properties();
        log(props);
        assertNotNull(props);
        assertThat(props.size(), is(2));
        assertThat(props.getProperty("a"), is("1"));
        assertThat(props.getProperty("b"), is("2"));
    }

    @Test
    public void loadExistingRoot() {
        ResourcePropertiesSource source = new ResourcePropertiesSource("/test2.properties");
        Properties props = source.properties();
        log(props);
        assertNotNull(props);
        assertThat(props.size(), is(2));
        assertThat(props.getProperty("c"), is("3"));
        assertThat(props.getProperty("d"), is("4"));
    }
}