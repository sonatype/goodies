/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.httpfixture.server.jetty.behaviour;

import java.io.File;

import org.sonatype.goodies.httpfixture.runner.junit.ConfigurationRunner;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.filesystem.Get;
import org.sonatype.goodies.httpfixture.server.jetty.util.FileUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link Get}.
 */
@RunWith(ConfigurationRunner.class)
public class GetTest
    extends BehaviourSuiteConfiguration<Get>
{
  private Get get = new Get();

  @Override
  public Get behaviour() {
    return get;
  }

  @Test
  public void testServe() throws Exception {
    File f = FileUtil.createTempFile("foo");

    behaviour().setPath(f.getParent());

    String url = url(f.getName());
    byte[] ba = fetch(url);

    Assert.assertArrayEquals("foo".getBytes("UTF-8"), ba);

    f.delete();
  }
}
