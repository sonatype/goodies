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
package org.sonatype.sisu.goodies.ssl.keystore.internal;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A X509TrustManager that will can be updated.  The default implementation is not updated when changes made to a
 * keystore.
 * <p/>
 * Based on work from: http://jcalcote.wordpress.com/2010/06/22/managing-a-dynamic-java-trust-store/
 *
 * @since 1.6
 */
public class ReloadableX509TrustManager
    implements X509TrustManager
{

  private X509TrustManager delegateTrustManager;

  /**
   * Creates the initial ReloadableX509TrustManager which wraps an existing X509TrustManager.
   *
   * @param delegateTrustManager the X509TrustManager to delegate calls to.
   */
  private ReloadableX509TrustManager(X509TrustManager delegateTrustManager) {
    this.setDelegateTrustManager(delegateTrustManager);
  }

  @Override
  public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
      throws CertificateException
  {
    delegateTrustManager.checkClientTrusted(x509Certificates, s);
  }

  @Override
  public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
      throws CertificateException
  {
    delegateTrustManager.checkServerTrusted(x509Certificates, s);
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return delegateTrustManager.getAcceptedIssuers();
  }

  /**
   * Sets the X509TrustManager which will be used to delegate calls to.
   *
   * @param delegateTrustManager the X509TrustManager which will be used to delegate calls to.
   */
  private void setDelegateTrustManager(X509TrustManager delegateTrustManager) {
    this.delegateTrustManager = delegateTrustManager;
  }

  /**
   * Finds and replaces the X509TrustManager with a ReloadableX509TrustManager.  If there is more then one, only the
   * first one will be replaced.
   *
   * @param reloadableX509TrustManager an existing ReloadableX509TrustManager, or null if one does not exist.
   * @param trustManagers              an array of TrustManagers that is expected to contain a X509TrustManager.
   * @return a newly create ReloadableX509TrustManager
   * @throws java.security.NoSuchAlgorithmException
   *                               thrown if a X509TrustManager cannot be found in the array.
   * @throws IllegalStateException thrown if a ReloadableX509TrustManager is found in the array.
   */
  public static ReloadableX509TrustManager replaceX509TrustManager(
      ReloadableX509TrustManager reloadableX509TrustManager, TrustManager[] trustManagers)
      throws NoSuchAlgorithmException, IllegalStateException
  {
    for (int ii = 0; ii < trustManagers.length; ii++) {
      if (ReloadableX509TrustManager.class.isInstance(trustManagers[ii])) {
        throw new IllegalStateException(
            "A ReloadableX509TrustManager has already been set for this TrustManager[]");
      }

      if (X509TrustManager.class.isInstance(trustManagers[ii])) {
        if (reloadableX509TrustManager == null) {
          reloadableX509TrustManager = new ReloadableX509TrustManager((X509TrustManager) trustManagers[ii]);
        }
        else {
          reloadableX509TrustManager.setDelegateTrustManager((X509TrustManager) trustManagers[ii]);
        }

        trustManagers[ii] = reloadableX509TrustManager;
        return reloadableX509TrustManager;
      }
    }

    throw new NoSuchAlgorithmException("No X509TrustManager found in TrustManager[]");
  }

}
