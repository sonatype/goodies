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
import java.util.Map;

import org.sonatype.sisu.goodies.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ParametersBuilder}
 */
public class ParametersBuilderTest
    extends TestSupport
{
  @Test
  public void empty() {
    Map<String, String> params = new ParametersBuilder().get();
    log(params);

    assertNotNull(params);
    assertThat(params.size(), is(0));
  }

  @Test
  public void mixedTypes() {
    Map<String, String> params = new ParametersBuilder()
        .set("a", "1")
        .set("b", 2)
        .get();
    log(params);

    assertNotNull(params);
    assertThat(params.size(), is(2));
    assertThat(params.get("a"), is("1"));
    assertThat(params.get("b"), is("2"));
  }

  @Test
  public void order() {
    Map<String, String> params = new ParametersBuilder()
        .set("a", "1")
        .set("b", 2)
        .get();
    log(params);

    assertNotNull(params);
    assertThat(params.size(), is(2));
    Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
    assertThat(iter.next().getKey(), is("a"));
    assertThat(iter.next().getKey(), is("b"));
  }
}
