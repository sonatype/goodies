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

package org.sonatype.sisu.goodies.ssl.keystore;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collection;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.sonatype.sisu.goodies.ssl.keystore.internal.geronimo.KeyNotFoundException;

import org.jetbrains.annotations.NonNls;

/**
 * Provides access to identity and trust stores.
 *
 * @since 1.6
 */
public interface KeyStoreManager
{

  /**
   * Returns an array of TrustManager that represents all client certificates that can connect.
   *
   * @throws KeystoreException thrown if the array of TrustManagers cannot be created
   */
  TrustManager[] getTrustManagers()
      throws KeystoreException;

  /**
   * Returns an array of KeyManagers used to handle the authentication with the remote peers.
   *
   * @throws KeystoreException thrown if the array of KeyManagers cannot be created
   */
  KeyManager[] getKeyManagers()
      throws KeystoreException;

  /**
   * Imports a clients public key that will be allowed to connect.
   *
   * @param certificate the public certificate to be added.
   * @param alias       the alias of the public key
   * @throws KeystoreException thrown if the certificate cannot be imported.
   */
  void importTrustCertificate(Certificate certificate, @NonNls String alias)
      throws KeystoreException;

  /**
   * Imports a clients public key that will be allowed to connect.
   *
   * @param certificateInPEM the public certificate to be added encoded in the PEM format.
   * @param alias            the alias of the public key
   * @throws KeystoreException thrown if the certificate cannot be imported.
   * @throws java.security.cert.CertificateException
   *                           thrown if the PEM formatted string cannot be parsed into a certificate.
   */
  void importTrustCertificate(@NonNls String certificateInPEM, @NonNls String alias)
      throws KeystoreException, CertificateException;

  /**
   * Returns a Certificate by an alias, that was previously stored in the keystore.
   *
   * @param alias the alias of the Certificate to be returned.
   * @return a previously imported Certificate.
   * @throws KeyNotFoundException thrown if a certificate with the given alias is not found.
   * @throws KeystoreException    thrown if there is a problem retrieving the certificate.
   */
  Certificate getTrustedCertificate(@NonNls String alias)
      throws KeystoreException;

  /**
   * Returns a collection of trusted certificates.
   *
   * @throws KeystoreException thrown if there is a problem opening the keystore.
   */
  Collection<Certificate> getTrustedCertificates()
      throws KeystoreException;

  /**
   * Removes a trusted certificate from the store.  Calling this method with an alias that does NOT exist will not
   * throw a KeystoreException.
   *
   * @param alias the alias of the certificate to be removed.
   * @throws KeystoreException thrown if the certificate by this alias cannot be removed or does not exist.
   */
  void removeTrustCertificate(@NonNls String alias)
      throws KeystoreException;

  /**
   * Generates and stores a key pair used for authenticating remote clients.
   *
   * @param commonName         typically a first and last name in the format of "Joe Coder"
   * @param organizationalUnit the organization unit of the user.
   * @param organization       the organization the user belongs to.
   * @param locality           city or locality of the user.
   * @param state              state or providence of the user.
   * @param country            two letter country code.
   * @throws KeystoreException thrown if the key pair cannot be created.
   */
  void generateAndStoreKeyPair(@NonNls String commonName, @NonNls String organizationalUnit,
                               @NonNls String organization, @NonNls String locality, @NonNls String state,
                               @NonNls String country)
      throws KeystoreException;

  /**
   * Returns true if the key pair has already been created, false otherwise.
   */
  boolean isKeyPairInitialized();

  /**
   * Returns a Certificate generated from the public and private key.
   * This certificate will be passed to a remote client before that remote client is able to authenticate.
   *
   * @throws KeystoreException thrown when the certificate has not been created.
   */
  Certificate getCertificate()
      throws KeystoreException;

  /**
   * Removes the private key from the KeyStore.
   *
   * @throws KeystoreException thrown if the KeyStore has not been initialized, the key could not be found, or an
   *                           error updating the KeyStore.
   */
  void removePrivateKey()
      throws KeystoreException;

  PrivateKey getPrivateKey()
      throws KeystoreException;
}
