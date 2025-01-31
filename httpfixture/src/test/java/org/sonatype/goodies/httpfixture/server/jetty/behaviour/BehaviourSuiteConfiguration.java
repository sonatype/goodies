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
package org.sonatype.goodies.httpfixture.server.jetty.behaviour;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.sonatype.goodies.httpfixture.runner.AbstractSuiteConfiguration;
import org.sonatype.goodies.httpfixture.server.api.Behaviour;
import org.sonatype.goodies.httpfixture.server.api.ServerProvider;

import org.junit.Before;

public abstract class BehaviourSuiteConfiguration<T extends Behaviour>
    extends AbstractSuiteConfiguration
{
  public static final class CustomTrustManager
      implements X509TrustManager
  {
    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
      // empty
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
      // empty
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }

  protected T behaviour;

  @Override
  @Before
  public void before() throws Exception {
    super.before();
  }

  @Override
  public void configureProvider(ServerProvider provider) {
    super.configureProvider(provider);
    provider.addBehaviour("/*", behaviour());
    // ( (JettyServerProvider) provider() ).addDefaultServices();
  }

  protected abstract T behaviour();

  protected byte[] fetch(String url) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newBuilder().sslContext(trustAllHttpsCertificates()).build();
    return client.send(HttpRequest.newBuilder(URI.create(url))
        .GET()
        .build(), BodyHandlers.ofByteArray())
        .body();
  }

  private static SSLContext trustAllHttpsCertificates() {
    SSLContext context;

    TrustManager[] _trustManagers = new TrustManager[]{new CustomTrustManager()};
    try {
      context = SSLContext.getInstance("SSL");
      context.init(null, _trustManagers, new SecureRandom());
    }
    catch (GeneralSecurityException gse) {
      throw new IllegalStateException(gse.getMessage());
    }
    return context;
  }
}