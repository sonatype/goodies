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
package org.sonatype.goodies.httpfixture.server.api;

import java.io.File;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.Servlet;

public interface ServerProvider
{
  void start() throws Exception;

  void stop() throws Exception;

  boolean isStarted();

  /**
   * The URL of the server.
   */
  URL getUrl();

  /**
   * Add the given chain of Behaviour to execute for the given pathspec.
   *
   * @param pathspec e.g. "/path/*"
   */
  void addBehaviour(String pathspec, Behaviour... behaviour);

  /**
   * Adds the given servlet for the given pathspec.
   */
  void addServlet(String pathSpec, Servlet servlet);

  /**
   * Adds the given filter for the given pathspec.
   */
  void addFilter(String pathSpec, Filter filter);

  /**
   * Mounts the given file context for the given pathspec.
   */
  void serveFiles(String pathSpec, FileContext fileContext);

  /**
   * The network interface to bind to. Defaults to "localhost" if not set.
   *
   * @param host The hostname or IP address to bind to, may be {@code null} to bind to all interfaces.
   */
  void setHost(String host);

  /**
   * Set to 0 to auto-choose a free port.
   */
  void setPort(int port);

  String getHost();

  int getPort();

  /**
   * @param keystore The keystore to use. (generated with keytool -keystore keystore -alias jetty -genkey -keyalg DSA')
   */
  void setSSL(String keystore, String password);

  /**
   * Add authentication handler to the given pathspec.
   *
   * @param pathSpec e.g. "/path/*"
   * @param authName e.g. BASIC, DIGEST
   */
  void addAuthentication(String pathSpec, String authName);

  /**
   * Add the given user and password to the servers security realm. The password may be any type supported by the
   * authentication type (e.g. a certificate for client side certificate auth).
   */
  void addUser(String user, Object password);

  void setSSLTruststore(String truststore, String password);

  void setSSLNeedClientAuth(boolean needClientAuth);

  /**
   * File serving context.
   */
  class FileContext
  {
    private final boolean collectionAllow;

    private final File baseDir;

    public FileContext(final File baseDir) {
      this(baseDir, true);
    }

    public FileContext(final File baseDir, final boolean collectionAllow) {
      this.collectionAllow = collectionAllow;
      this.baseDir = baseDir;
    }

    public boolean isCollectionAllow() {
      return collectionAllow;
    }

    public File getBaseDir() {
      return baseDir;
    }
  }
}
