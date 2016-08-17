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
package org.sonatype.goodies.httpfixture.server.fluent;

import java.io.File;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.sonatype.goodies.httpfixture.server.api.Behaviour;
import org.sonatype.goodies.httpfixture.server.api.ServerProvider;
import org.sonatype.goodies.httpfixture.server.api.ServerProvider.FileContext;
import org.sonatype.goodies.httpfixture.server.jetty.impl.JettyServerProvider;

import static com.google.common.base.Preconditions.checkNotNull;

public class Server
{
  private final ServerProvider serverProvider;

  public Server() {
    this(new JettyServerProvider());
  }

  public Server(final ServerProvider serverProvider) {
    this.serverProvider = checkNotNull(serverProvider);
  }

  public ServerProvider getServerProvider() {
    return serverProvider;
  }

  /**
   * Start a server on a random port.
   */
  public static Server server() {
    return new Server();
  }

  /**
   * Start a server on the given port (0 for random).
   */
  public static Server withPort(int port) {
    JettyServerProvider jettyServerProvider = new JettyServerProvider();
    jettyServerProvider.setPort(port);
    return new Server(jettyServerProvider);
  }

  /**
   * Set the port for the server.
   *
   * @throws IllegalStateException if the server is already started.
   */
  public Server port(int port) throws IllegalStateException {
    if (serverProvider.isStarted()) {
      throw new IllegalStateException("Server is currently running, cannot change port.");
    }
    serverProvider.setPort(port);

    return this;
  }

  public Server withHost(final String host) {
    if (serverProvider.isStarted()) {
      throw new IllegalStateException("Server is currently running, cannot change host.");
    }
    serverProvider.setHost(host);
    return this;
  }

  public Server withKeystore(final String keystore, final String password) {
    serverProvider.setSSL(keystore, password);
    return this;
  }

  public Server withTruststore(final String truststore, final String password) {
    serverProvider.setSSLTruststore(truststore, password);
    return this;
  }

  public Server requireClientAuth() {
    serverProvider.setSSLNeedClientAuth(true);
    return this;
  }

  public ServeContext serve(String pattern) {
    return new ServeContext(this, pattern);
  }

  public Server start() throws Exception {
    serverProvider.start();
    return this;
  }

  public void stop() throws Exception {
    serverProvider.stop();
  }

  public int getPort() {
    return serverProvider.getPort();
  }

  public URL getUrl() {
    return serverProvider.getUrl();
  }

  // ==

  public static class ServeContext
  {
    private final String pattern;

    private final Server server;

    ServeContext(final Server server, final String pattern) {
      this.pattern = checkNotNull(pattern);
      this.server = checkNotNull(server);
    }

    public Server withBehaviours(Behaviour... behaviours) {
      checkNotNull(behaviours);
      server.getServerProvider().addBehaviour(pattern, behaviours);
      return server;
    }

    public Server withServlet(Servlet servlet) {
      checkNotNull(servlet);
      server.getServerProvider().addServlet(pattern, servlet);
      return server;
    }

    public Server withFilter(Filter filter) {
      checkNotNull(filter);
      server.getServerProvider().addFilter(pattern, filter);
      return server;
    }

    public Server fromDirectory(File directory) {
      checkNotNull(directory);
      server.getServerProvider().serveFiles(pattern, new FileContext(directory));
      return server;
    }
  }
}
