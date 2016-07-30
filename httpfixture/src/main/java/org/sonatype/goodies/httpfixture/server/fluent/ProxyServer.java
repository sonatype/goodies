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

import org.sonatype.goodies.httpfixture.validation.Validator;

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

/**
 * Provides a real proxy server using the little proxy project. Start and retrieve an {@link HttpProxyServer}
 * by calling method {@link HttpProxyServerBootstrap#start()} on the returned {@link HttpProxyServerBootstrap},
 * and stop it with {@link HttpProxyServer#stop()}.
 *
 * See: https://github.com/adamfisk/LittleProxy
 *
 * @since 2.2.0
 */
public class ProxyServer
{

  /**
   * Generate an {@link HttpProxyServerBootstrap} which validates requests using the given {@link Validator}
   * object.
   */
  public static HttpProxyServerBootstrap createWithValidation(final Validator... validation) {
    return DefaultHttpProxyServer.bootstrap().withAllowLocalOnly(true).withAuthenticateSslClients(false).withPort(0)
        .withFiltersSource(new HttpFiltersSourceAdapter()
        {
          @Override
          public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            return new HttpFiltersAdapter(originalRequest)
            {
              @Override
              public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                for (Validator v : validation) {
                  v.validate(originalRequest);
                }
                return null;
              }
            };
          }
        });
  }

}
