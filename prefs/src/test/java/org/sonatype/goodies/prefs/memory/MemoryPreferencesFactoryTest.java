/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.prefs.memory;

import java.util.prefs.Preferences;

import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link MemoryPreferencesFactory}.
 */
public class MemoryPreferencesFactoryTest
    extends TestSupport
{
  private MemoryPreferencesFactory factory;

  @Before
  public void setUp() throws Exception {
    factory = new MemoryPreferencesFactory();
  }

  @Test
  public void getSystemRoot() {
    Preferences pref = factory.systemRoot();
    assertThat(pref, notNullValue());
    assertThat(pref.name(), is(MemoryPreferences.ROOT_NAME));

    // should return same instance
    assertThat(pref, is(factory.systemRoot()));
  }

  @Test
  public void getUserRoot() {
    Preferences pref = factory.userRoot();
    assertThat(pref, notNullValue());
    assertThat(pref.name(), is(MemoryPreferences.ROOT_NAME));

    // should return same instance
    assertThat(pref, is(factory.userRoot()));
  }
}
