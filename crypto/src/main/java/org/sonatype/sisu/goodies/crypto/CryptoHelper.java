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
package org.sonatype.sisu.goodies.crypto;

import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Provides access to cryptology components.
 *
 * @since 1.3
 */
public interface CryptoHelper
{
  Cipher createCipher(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException;

  /**
   * @since 1.5
   */
  int getCipherMaxAllowedKeyLength(String transformation) throws NoSuchAlgorithmException;

  Signature createSignature(String algorithm) throws NoSuchAlgorithmException;

  SecureRandom createSecureRandom(String algorithm) throws NoSuchAlgorithmException;

  SecureRandom createSecureRandom();

  KeyStore createKeyStore(String type) throws KeyStoreException;

  KeyPairGenerator createKeyPairGenerator(String algorithm) throws NoSuchAlgorithmException;

  CertificateFactory createCertificateFactory(String type) throws CertificateException;

  KeyManagerFactory createKeyManagerFactory(String algorithm) throws NoSuchAlgorithmException;

  TrustManagerFactory createTrustManagerFactory(String algorithm) throws NoSuchAlgorithmException;

  MessageDigest createDigest(String algorithm) throws NoSuchAlgorithmException;

  /**
   * @since 1.10
   */
  SecretKeyFactory createSecretKeyFactory(String algorithm) throws NoSuchAlgorithmException;
}

