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

import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;

/**
 * A X509KeyManager that will can be updated.  The default implementation is not updated when changes made to a
 * keystore.
 *
 * @see ReloadableX509TrustManager
 * @since 1.6
 */
public class ReloadableX509KeyManager
    implements X509KeyManager
{

  private X509KeyManager delegateKeyManager;

  /**
   * Creates the initial ReloadableX509KeyManager which wraps an existing X509KeyManager.
   *
   * @param delegateKeyManager the X509KeyManager to delegate calls to.
   */
  private ReloadableX509KeyManager(X509KeyManager delegateKeyManager) {
    this.setDelegateKeyManager(delegateKeyManager);
  }

  @Override
  public String[] getClientAliases(String s, Principal[] principals) {
    return delegateKeyManager.getClientAliases(s, principals);
  }

  @Override
  public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
    return delegateKeyManager.chooseClientAlias(strings, principals, socket);
  }

  @Override
  public String[] getServerAliases(String s, Principal[] principals) {
    return delegateKeyManager.getServerAliases(s, principals);
  }

  @Override
  public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
    return delegateKeyManager.chooseServerAlias(s, principals, socket);
  }

  @Override
  public X509Certificate[] getCertificateChain(String s) {
    return delegateKeyManager.getCertificateChain(s);
  }

  @Override
  public PrivateKey getPrivateKey(String s) {
    return delegateKeyManager.getPrivateKey(s);
  }

  /**
   * Sets the X509KeyManager which will be used to delegate calls to.
   *
   * @param delegateKeyManager the X509KeyManager which will be used to delegate calls to.
   */
  private void setDelegateKeyManager(X509KeyManager delegateKeyManager) {
    this.delegateKeyManager = delegateKeyManager;
  }

  /**
   * Finds and replaces the X509KeyManager with a ReloadableX509KeyManager.  If there is more then one, only the first
   * one will be replaced.
   *
   * @param reloadableX509KeyManager an existing ReloadableX509KeyManager, or null if one does not exist.
   * @param KeyManagers              an array of KeyManagers that is expected to contain a X509KeyManager.
   * @return a newly create ReloadableX509KeyManager
   * @throws java.security.NoSuchAlgorithmException
   *                               thrown if a X509KeyManager cannot be found in the array.
   * @throws IllegalStateException thrown if a ReloadableX509KeyManager is found in the array.
   */
  public static ReloadableX509KeyManager replaceX509KeyManager(ReloadableX509KeyManager reloadableX509KeyManager,
                                                               KeyManager[] KeyManagers)
      throws NoSuchAlgorithmException, IllegalStateException
  {
    for (int ii = 0; ii < KeyManagers.length; ii++) {
      if (ReloadableX509KeyManager.class.isInstance(KeyManagers[ii])) {
        throw new IllegalStateException(
            "A ReloadableX509KeyManager has already been set for this KeyManager[]");
      }

      if (X509KeyManager.class.isInstance(KeyManagers[ii])) {
        if (reloadableX509KeyManager == null) {
          reloadableX509KeyManager = new ReloadableX509KeyManager((X509KeyManager) KeyManagers[ii]);
        }
        else {
          reloadableX509KeyManager.setDelegateKeyManager((X509KeyManager) KeyManagers[ii]);
        }

        KeyManagers[ii] = reloadableX509KeyManager;
        return reloadableX509KeyManager;
      }
    }

    throw new NoSuchAlgorithmException("No X509KeyManager found in KeyManager[]");
  }

}
