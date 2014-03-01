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

import java.io.File;
import java.util.Properties;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link FilePropertiesSource}.
 */
public class FilePropertiesSourceTest
    extends TestSupport
{
  @Test
  public void loadExisting() {
    File file = util.resolveFile(
        "src/test/resources/" + getClass().getPackage().getName().replace(".", "/") + "/test1.properties"); // a=1 b=2
    log(file);
    assertThat(file.exists(), is(true));

    FilePropertiesSource source = new FilePropertiesSource(file);
    Properties props = source.properties();
    log(props);
    assertNotNull(props);
    assertThat(props.size(), is(2));
    assertThat(props.getProperty("a"), is("1"));
    assertThat(props.getProperty("b"), is("2"));
  }

  @Test
  public void loadMissing() {
    File file = util.resolveFile("target/no-such-file");
    FilePropertiesSource source = new FilePropertiesSource(file);
    Properties props = source.properties();
    log(props);
    assertNotNull(props);
    assertThat(props.size(), is(0));
  }
}