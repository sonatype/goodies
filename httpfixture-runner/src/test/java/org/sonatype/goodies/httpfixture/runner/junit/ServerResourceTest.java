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
package org.sonatype.goodies.httpfixture.runner.junit;

import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link ServerResource}.
 */
public class ServerResourceTest
    extends TestSupport
{
  @Rule
  public ServerResource server = new ServerResource(new DummyProvider());

  @Test
  public void succeed() throws Exception {
    assertThat(server.getServerProvider().isStarted(), is(true));
    server.getServerProvider().stop();
    assertThat(server.getServerProvider().isStarted(), is(false));
  }
}
