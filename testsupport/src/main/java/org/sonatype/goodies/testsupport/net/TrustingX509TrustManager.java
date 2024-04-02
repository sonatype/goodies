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
package org.sonatype.goodies.testsupport.net;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * A very naive TrustManager.
 * Trusts every certificate it comes across.
 *
 * @since litmus 1.4
 */
@IgnoreJRERequirement
public class TrustingX509TrustManager
    extends X509ExtendedTrustManager
{
  @Override
  public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
      throws CertificateException
  {

  }

  @Override
  public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
      throws CertificateException
  {

  }

  @Override
  public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
      throws CertificateException
  {

  }

  @Override
  public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
      throws CertificateException
  {

  }

  @Override
  public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {

  }

  @Override
  public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {

  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
  }
}

