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

import java.util.Arrays;
import java.util.List;

import org.sonatype.goodies.httpfixture.validation.HttpValidator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides a real proxy server using the little proxy project, adding support for validation on an http request.
 * Start with {@link #start()} and stop with {@link #stop()}, usually in the before/after or beforeclass/afterclass
 * methods of a test.
 *
 * See: https://github.com/adamfisk/LittleProxy
 *
 * @since 2.2.0
 */
public class ProxyServer
{
  private HttpProxyServer server;

  private final HttpProxyServerBootstrap bootstrap;

  private final List<HttpValidator> validators;

  public ProxyServer(HttpValidator... validators) {
    checkState(validators.length > 0, "Must have at least one validator");
    this.validators = Arrays.asList(validators);
    bootstrap = createWithValidation(validators);
  }

  public void start() {
    checkNotNull(bootstrap);
    server = bootstrap.start();
  }

  public void stop() {
    checkNotNull(server);
    server.stop();
  }

  public List<HttpValidator> getValidators() {
    return validators;
  }

  /**
   * Get the host name without port or protocol.
   */
  public String getHostName() {
    checkNotNull(server);

    return server.getListenAddress().getHostName();
  }

  public int getPort() {
    checkNotNull(server);

    return server.getListenAddress().getPort();
  }

  /**
   * Generate an {@link HttpProxyServerBootstrap} which validates requests using the given {@link HttpValidator}
   * object.
   */
  private HttpProxyServerBootstrap createWithValidation(final HttpValidator... validation) {
    return DefaultHttpProxyServer.bootstrap().withAllowLocalOnly(true).withAuthenticateSslClients(false).withPort(0)
        .withFiltersSource(new HttpFiltersSourceAdapter()
        {
          @Override
          public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            return new HttpFiltersAdapter(originalRequest)
            {
              @Override
              public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                for (HttpValidator v : validation) {
                  v.validate(originalRequest);
                }
                return null;
              }
            };
          }
        });
  }

}
