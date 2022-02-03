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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.sonatype.goodies.httpfixture.server.api.Behaviour;
import org.sonatype.goodies.httpfixture.server.api.ServerProvider;

import com.google.common.base.Throwables;

/**
 * Dummy {@link ServerProvider}.
 */
class DummyProvider
    implements ServerProvider
{
  private boolean started;

  public URL getUrl() {
    try {
      return URI.create("dummy://url").toURL();
    }
    catch (MalformedURLException e) {
      throw propagate(e);
    }
  }

  public void stop() throws Exception {
    started = false;
  }

  public void addBehaviour(String pathspec, Behaviour... behaviour) {
    // empty
  }

  @Override
  public void addServlet(final String pathSpec, final Servlet servlet) {
    // empty
  }

  @Override
  public void addFilter(final String pathSpec, final Filter filter) {
    // empty
  }

  @Override
  public void serveFiles(final String pathSpec, final FileContext fileContext) {
    // empty
  }

  public void start() throws Exception {
    started = true;
  }

  public void initServer() throws Exception {
    // empty
  }

  public void setPort(int port) {
    // empty
  }

  public int getPort() {
    return -1;
  }

  @Override
  public void setHost(String host) {
    // empty
  }

  public void setSSL(String keystore, String password) {
    // empty
  }

  public void addAuthentication(String pathSpec, String authName) {
    // empty
  }

  public void addUser(String user, Object password) {
    // empty
  }

  public boolean isStarted() {
    return started;
  }

  public void setSSLTruststore(final String truststore, final String password) {
    // empty
  }

  public void setSSLNeedClientAuth(final boolean needClientAuth) {
    // empty
  }

  private static RuntimeException propagate(Throwable throwable) {
    Throwables.throwIfUnchecked(throwable);
    throw new RuntimeException(throwable);
  }
}
