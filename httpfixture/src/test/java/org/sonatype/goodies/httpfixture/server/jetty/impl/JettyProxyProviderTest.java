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
package org.sonatype.goodies.httpfixture.server.jetty.impl;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;

import javax.annotation.Nullable;

import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Consumer;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Content;
import org.sonatype.goodies.httpfixture.server.jetty.behaviour.Debug;
import org.sonatype.goodies.testsupport.TestSupport;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests for {@link JettyProxyProvider}.
 */
public class JettyProxyProviderTest
    extends TestSupport
{
  private static final URI FOO = URI.create("http://speutel.invalid/foo");

  @Test
  public void testProxyGet() throws Exception {
    JettyProxyProvider proxy = new JettyProxyProvider();
    proxy.addBehaviour("/*", new Debug(), new Content());
    proxy.start();

    HttpClient client = client(proxy);

    HttpResponse<String> response = client.send(get(FOO), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("foo", response.body().trim());
  }

  @Test
  public void testProxyAuthGet() throws Exception {
    JettyProxyProvider proxy = new JettyProxyProvider("u", "p");
    proxy.addBehaviour("/*", new Debug(), new Content());
    proxy.start();


    HttpClient client = client(proxy, new Authenticator()
    {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        if (getRequestingHost().equals("localhost")) {
          String password = "p";
          return new PasswordAuthentication("u", password.toCharArray());
        }
        return super.getPasswordAuthentication();
      }
    });

    HttpResponse<String> response = client.send(get(FOO), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals("foo", response.body().trim());
  }

  @Test
  public void testProxyAuthGetFail407() throws Exception {
    JettyProxyProvider proxy = new JettyProxyProvider("u", "p");
    proxy.addBehaviour("/*", new Debug(), new Content());
    proxy.start();

    HttpClient client = client(proxy);

    HttpResponse<String> response = client.send(get(FOO), BodyHandlers.ofString());
    assertEquals(407, response.statusCode());
  }

  @Test
  public void testAuthAfterProxyAuthGetFail401() throws Exception {
    JettyProxyProvider proxy = new JettyProxyProvider("u", "p");
    proxy.addBehaviour("/*", new Debug(), new Content());
    proxy.addAuthentication("/*", "BASIC");
    proxy.addUser("user", "password");
    proxy.start();

    HttpClient client = client(proxy, new Authenticator()
    {
      @Override
      protected PasswordAuthentication getPasswordAuthentication()
      {
        if (getRequestingHost().equals("localhost")) {
          String password = "p";
          return new PasswordAuthentication("u", password.toCharArray());
        }
        return super.getPasswordAuthentication();
      }
    });

    HttpRequest request = get(FOO);
    BodyHandler<Void> discarding = BodyHandlers.discarding();

    assertThrows("No credentials provided", IOException.class, () -> client.send(request, discarding));
  }

  @Test
  public void testAuthAfterProxyAuthGet() throws Exception {
    JettyProxyProvider proxy = new JettyProxyProvider("u", "p");
    proxy.addBehaviour("/*", new Debug(), new Content());
    proxy.addAuthentication("/*", "BASIC");
    proxy.addUser("user", "password");
    proxy.start();

    HttpClient client = client(proxy, new Authenticator()
    {
      @Override
      protected PasswordAuthentication getPasswordAuthentication()
      {
        if (getRequestingHost().equals("localhost")) {
          String password = "p";
          return new PasswordAuthentication("u", password.toCharArray());
        }
        else {
          String password = "password";
          return new PasswordAuthentication("user", password.toCharArray());

        }
      }
    });

    HttpResponse<String> response = client.send(get(FOO), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
  }

  @Test
  public void testProxyPut() throws Exception {
    JettyProxyProvider proxy = new JettyProxyProvider();
    Consumer consumer = new Consumer();
    proxy.addBehaviour("/*", new Debug(), consumer);
    proxy.start();

    byte[] bytes = "TestPut".getBytes("US-ASCII");
    HttpClient client = client(proxy);

    HttpResponse<String> response = client.send(HttpRequest.newBuilder()
        .uri(FOO)
        .PUT(BodyPublishers.ofByteArray(bytes))
        .build(),
        BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    assertEquals(bytes.length, consumer.getTotal());
  }

  private static HttpClient client(JettyProxyProvider proxy, @Nullable Authenticator authenticator) {
    InetSocketAddress sa = new InetSocketAddress("localhost", proxy.getPort());
    HttpClient.Builder builder=  HttpClient
        .newBuilder()
        .proxy(ProxySelector.of(sa));

    if (authenticator != null) {
      builder.authenticator(authenticator);
    }

    return builder.build();
  }

  private static HttpRequest get(URI uri) throws URISyntaxException {
    return HttpRequest.newBuilder().uri(uri).GET().build();
  }

  private static HttpClient client(JettyProxyProvider proxy) {
    return client(proxy, null);
  }
}
