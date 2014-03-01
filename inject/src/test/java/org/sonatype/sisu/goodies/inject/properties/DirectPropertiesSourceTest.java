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
package org.sonatype.sisu.goodies.inject.properties;

import java.util.Properties;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DirectPropertiesSource}.
 */
public class DirectPropertiesSourceTest
    extends TestSupport
{
  @Test
  public void load() {
    Properties props1 = new Properties();
    props1.setProperty("a", "1");
    props1.setProperty("b", "2");
    log(props1);
    DirectPropertiesSource source = new DirectPropertiesSource(props1);
    Properties props2 = source.properties();
    log(props2);
    assertNotNull(props2);
    assertThat(props2, is(props1));
  }
}