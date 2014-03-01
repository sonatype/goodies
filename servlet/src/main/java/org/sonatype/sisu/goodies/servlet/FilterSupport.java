/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.goodies.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.sisu.goodies.common.ComponentSupport;

/**
 * Support for {@link Filter} implementations.
 *
 * @since 1.5
 */
public class FilterSupport
    extends ComponentSupport
    implements Filter
{
  @Override
  public void init(final FilterConfig config) throws ServletException {
    // empty
  }

  @Override
  public void destroy() {
    // empty
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException
  {
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }
    else {
      chain.doFilter(request, response);
    }
  }

  protected void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws IOException, ServletException
  {
    chain.doFilter(request, response);
  }
}
