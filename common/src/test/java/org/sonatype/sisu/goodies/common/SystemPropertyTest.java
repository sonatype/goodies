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

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link SystemProperty}.
 */
public class SystemPropertyTest
{
  private SystemProperty underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new SystemProperty("test")
    {
      // use private properties instead of system to avoid side-effects
      final Properties props = new Properties();
      @Override
      Properties properties() {
        return props;
      }
    };
  }

  @Test
  public void verifySystemPropertySet() {
    // sanity check since we are using private properties for all other tests
    String name = "test" + System.currentTimeMillis();
    try {
      assertThat(System.getProperty(name), nullValue());
      new SystemProperty(name).set("foo");
      assertThat(System.getProperty(name), is("foo"));
    }
    finally {
      System.getProperties().remove(name);
    }
  }

  @Test
  public void typeSuffixName() {
    SystemProperty prop = new SystemProperty(Object.class, "foo");
    assertThat(prop.name(), is("java.lang.Object.foo"));
  }

  @Test
  public void setGetRemove() {
    assertThat(underTest.isSet(), is(false));
    underTest.set("foo");
    assertThat(underTest.isSet(), is(true));
    assertThat(underTest.get(), is("foo"));
    underTest.remove();
    assertThat(underTest.get(), nullValue());
    assertThat(underTest.isSet(), is(false));
  }

  @Test
  public void get_withDefault() {
    assertThat(underTest.get("default"), is("default"));
    underTest.set("foo");
    assertThat(underTest.get("default"), is("foo"));
  }

  @Test
  public void get_asInteger() {
    assertThat(underTest.get(Integer.class), nullValue());
    underTest.set("1");
    assertThat(underTest.get(Integer.class), is(1));
  }

  @Test
  public void get_asInteger_withDefault() {
    assertThat(underTest.get(Integer.class, 1), is(1));
    underTest.set("2");
    assertThat(underTest.get(Integer.class, 1), is(2));
  }

  @Test
  public void get_asBoolean_withDefault() {
    assertThat(underTest.get(Boolean.class, false), is(false));
    underTest.set("true");
    assertThat(underTest.get(Boolean.class, false), is(true));
  }

  @Test
  public void asList_missingProperty() {
    List<String> result = underTest.asList();
    assertThat(result, notNullValue());
    assertThat(result, empty());
  }

  @Test
  public void asList_singleValue() {
    underTest.set("foo");
    List<String> result = underTest.asList();
    assertThat(result, notNullValue());
    assertThat(result, hasSize(1));
    assertThat(result, hasItem("foo"));
  }

  @Test
  public void asList_multiValue() {
    underTest.set(" foo, bar ,\nbaz\n  ");
    List<String> result = underTest.asList();
    assertThat(result, notNullValue());
    assertThat(result, hasSize(3));
    assertThat(result, hasItem("foo"));
    assertThat(result, hasItem("bar"));
    assertThat(result, hasItem("baz"));
  }
}
