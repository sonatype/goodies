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

  @Override
  public URL getUrl() {
    try {
      return URI.create("dummy://url").toURL();
    }
    catch (MalformedURLException e) {
      throw propagate(e);
    }
  }

  @Override
  public void stop() throws Exception {
    started = false;
  }

  @Override
  public void addBehaviour(final String pathspec, final Behaviour... behaviour) {
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

  @Override
  public void start() throws Exception {
    started = true;
  }

  public void initServer() throws Exception {
    // empty
  }

  @Override
  public void setPort(final int port) {
    // empty
  }

  @Override
  public int getPort() {
    return -1;
  }

  @Override
  public String getHost() {
    return null;
  }

  @Override
  public void setHost(final String host) {
    // empty
  }

  @Override
  public void setSSL(final String keystore, final String password) {
    // empty
  }

  @Override
  public void addAuthentication(final String pathSpec, final String authName) {
    // empty
  }

  @Override
  public void addUser(final String user, final Object password) {
    // empty
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public void setSSLTruststore(final String truststore, final String password) {
    // empty
  }

  @Override
  public void setSSLNeedClientAuth(final boolean needClientAuth) {
    // empty
  }

  private static RuntimeException propagate(final Throwable throwable) {
    Throwables.throwIfUnchecked(throwable);
    throw new RuntimeException(throwable);
  }
}
