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

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.HostAndPort;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.DefaultHostResolver;
import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger log = LoggerFactory.getLogger(ValidatingProxyServer.class);

  private HttpProxyServer server;

  private AtomicInteger successCount = new AtomicInteger();

  private int serverPort = 0;

  private final HttpValidator[] validators;

  public ValidatingProxyServer(HttpValidator... validators) {
    ValidationUtil.verifyValidators(validators);

    this.validators = validators;
  }

  public void start() {
    server = createWithValidation(validators).start();
  }

  public boolean isStarted() {
    return server != null;
  }

  public void stop() {
    verifyServerState();

    server.stop();
  }

  /**
   * Get the host name without port or protocol.
   */
  public String getHostName() {
    verifyServerState();

    return server.getListenAddress().getHostName();
  }

  public int getPort() {
    verifyServerState();

    return server.getListenAddress().getPort();
  }

  public ValidatingProxyServer withPort(int port) {
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
  private HttpProxyServerBootstrap createWithValidation(final HttpValidator... validators) { // NOSONAR
    return DefaultHttpProxyServer.bootstrap()
        .withPort(serverPort)
        .withAllowLocalOnly(true)
        .withAuthenticateSslClients(false)
        .withFiltersSource(new HttpFiltersSourceAdapter()
        {
          @Override
          public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            return new HttpFiltersAdapter(originalRequest)
            {
              private final HostResolver hostResolver = new DefaultHostResolver();

              @Override
              public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                HttpServletRequest req = new NettyHttpRequestWrapper(originalRequest);
                for (HttpValidator v : validators) {
                  v.validate(req);
                }
                successCount.incrementAndGet();
                return null;
              }

              /**
               * Workaround https://github.com/adamfisk/LittleProxy/issues/398 by overriding default parsing.
               */
              @Override
              public InetSocketAddress proxyToServerResolutionStarted(final String resolvingServerHostAndPort) {
                try {
                  HostAndPort hostAndPort = HostAndPort.fromString(resolvingServerHostAndPort);
                  String host = getHost(hostAndPort);
                  int port = hostAndPort.getPortOrDefault(80);
                  return hostResolver.resolve(host, port);
                }
                catch (Exception e) {
                  log.warn("Problem resolving {}", resolvingServerHostAndPort, e);
                  return null; // we can't resolve this host, return null to fall back to normal DNS resolution
                }
              }

              private String getHost(final HostAndPort hostAndPort) {
                try {
                  return hostAndPort.getHost();
                }
                catch (LinkageError e) {
                  return hostAndPort.getHostText(); // fall back to deprecated method on old versions of Guava
                }
              }
            };
          }
        });
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
