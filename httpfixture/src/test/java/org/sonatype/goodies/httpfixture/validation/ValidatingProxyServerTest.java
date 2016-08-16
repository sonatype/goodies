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
package org.sonatype.goodies.httpfixture.validation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.sonatype.goodies.httpfixture.server.fluent.Behaviours;
import org.sonatype.goodies.httpfixture.server.fluent.Server;
import org.sonatype.goodies.testsupport.port.PortRegistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidatingProxyServerTest
{

  private ValidatingProxyServer underTest;

  private static PortRegistry portRegistry = new PortRegistry();

  @Mock
  private HttpValidator validator;

  @Captor
  private ArgumentCaptor<HttpServletRequest> validatorCaptor;

  private static Server server = Server.server().port(portRegistry.reservePort());

  @BeforeClass
  public static void beforeClass() throws Exception {
    server.start().serve("").withBehaviours(Behaviours.content(""));
  }

  @AfterClass
  public static void afterClass() throws Exception {
    server.stop();
  }

  @Before
  public void before() {
    underTest = new ValidatingProxyServer(validator);
  }

  @After
  public void after() {
    if (underTest.isStarted()) {
      underTest.stop();
    }
  }

  @Test
  public void testRequestHappyPathWithPort() throws IOException {
    testRequestHappyPath(portRegistry.reservePort());
  }

  @Test
  public void testRequestHappyPathDefaultPort() throws IOException {
    testRequestHappyPath(null);
  }

  private void testRequestHappyPath(Integer port) throws IOException {
    // Setup
    if (port != null) {
      underTest.withPort(port);
    }
    underTest.start();

    if (port != null) {
      assertThat(underTest.getPort(), equalTo(port));
    }
    assertThat(underTest.getSuccessCount(), equalTo(0));

    URL url = server.getUrl();
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(underTest.getHostName(), underTest.getPort()));

    // Execute
    HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);

    // Verify
    assertThat(conn.getResponseCode(), equalTo(200));

    verify(validator, times(2)).validate(validatorCaptor.capture());
    assertThat(validatorCaptor.getValue(), instanceOf(NettyHttpRequestWrapper.class));

    assertThat(underTest.getSuccessCount(), equalTo(2));
  }

}
