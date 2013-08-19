/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
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
package com.sonatype.maven.settings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;

/**
 * Static helpers to access configuration from maven settings.xml.
 * 
 * @since 1.7
 */
public class MavenSettingsHelper
{
  private MavenSettingsHelper() {
  }

  /**
   * Matches {@code serverUrl} to a proxy element in settings.
   * 
   * @return matching proxy or null if there is no such proxy.
   * @since 1.7
   */
  public static Proxy selectProxy(final Settings settings, final String serverUrl) throws MalformedURLException {
    URL url = new URL(serverUrl);
    String host = url.getHost();

    Proxy httpProxy = null;
    Proxy httpsProxy = null;
    Collection<Proxy> proxies = settings.getProxies();
    for (Proxy proxy : proxies) {
      if (proxy.isActive() && !isNonProxyHosts(host, proxy.getNonProxyHosts())) {
        if ("http".equalsIgnoreCase(proxy.getProtocol()) && httpProxy == null) {
          httpProxy = proxy;
        }
        else if ("https".equalsIgnoreCase(proxy.getProtocol()) && httpsProxy == null) {
          httpsProxy = proxy;
        }
      }
    }

    Proxy proxy = httpProxy;
    if ("https".equalsIgnoreCase(url.getProtocol()) && httpsProxy != null) {
      proxy = httpsProxy;
    }

    return proxy;
  }

  private static boolean isNonProxyHosts(String host, String nonProxyHosts) {
    if (host != null && nonProxyHosts != null && nonProxyHosts.length() > 0) {
      for (StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|"); tokenizer.hasMoreTokens();) {
        String pattern = tokenizer.nextToken();
        pattern = pattern.replace(".", "\\.").replace("*", ".*");
        if (host.matches(pattern)) {
          return true;
        }
      }
    }

    return false;
  }

}
