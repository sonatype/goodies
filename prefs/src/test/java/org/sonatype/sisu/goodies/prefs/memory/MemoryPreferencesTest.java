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
package org.sonatype.sisu.goodies.prefs.memory;

import org.junit.Before;
import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link MemoryPreferences}.
 */
public class MemoryPreferencesTest
    extends TestSupport
{
    private MemoryPreferences root;

    @Before
    public void setUp() throws Exception {
        root = new MemoryPreferences();
    }

    @Test
    public void getSetRemoveValue() {
        String key = "foo";
        String value = root.get(key, null);
        assertThat(value, nullValue());
        root.put(key, "bar");
        value = root.get(key, null);
        assertThat(value, is("bar"));
        root.remove(key);
        value = root.get(key, null);
        assertThat(value, nullValue());
    }
}
