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
package org.sonatype.goodies.httpfixture.validation;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.goodies.httpfixture.server.fluent.Server;

import org.eclipse.jetty.ee8.proxy.ProxyServlet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides a real proxy server using the little proxy project for use in validating an http request.
 * Start with {@link #start()} and stop with {@link #stop()}, usually in the before/after or beforeclass/afterclass
 * methods of a test.
 *
 * See: https://github.com/adamfisk/LittleProxy
 *
 * @since 2.2.0
 */
public class ValidatingProxyServer
{
  private Server server;

  private AtomicInteger successCount = new AtomicInteger();

  private int serverPort = 0;

  private final HttpValidator[] validators;

  public ValidatingProxyServer(final HttpValidator... validators) {
    ValidationUtil.verifyValidators(validators);

    this.validators = validators;
  }

  public void start() {
    try {
      server = createWithValidation(validators).start();
    }
    catch (Exception e) {
      propagate(e);
    }
  }

  public boolean isStarted() {
    return server != null;
  }

  public void stop() {
    verifyServerState();

    try {
      server.stop();
    }
    catch (Exception e) {
      propagate(e);
    }
  }

  private void propagate(final Exception e) {
    if (e instanceof RuntimeException re) {
      throw re;
    }
    throw new RuntimeException(e);
  }

  /**
   * Get the host name without port or protocol.
   */
  public String getHostName() {
    verifyServerState();

    return server.getHost();
  }

  public int getPort() {
    verifyServerState();

    return server.getPort();
  }

  public ValidatingProxyServer withPort(final int port) {
    checkArgument(port > 0, "Must have port greater than zero.");
    this.serverPort = port;

    return this;
  }

  private void verifyServerState() {
    checkState(server != null, "Server not started.");
  }

  /**
   * Generate an {@link HttpProxyServerBootstrap} which validates requests using the given {@link HttpValidator}
   * object(s).
   */
  private Server createWithValidation(final HttpValidator... validators) { // NOSONAR
    return Server.withPort(serverPort)
        .serve("/*")
        .withFilter(new HttpFilter()
        {
          @Override
          protected void doFilter(
              final HttpServletRequest req,
              final HttpServletResponse res,
              final FilterChain chain) throws IOException, ServletException
          {
            for (HttpValidator v : validators) {
              v.validate(req);
            }
            successCount.incrementAndGet();
            chain.doFilter(req, res);
          }
        })
        .serve("/*")
        .withServlet(new ProxyServlet());
  }

  /**
   * Get number of successful validations.
   */
  public int getSuccessCount() {
    return successCount.get();
  }

  public void resetSuccessCount() {
    successCount.set(0);
  }

}
