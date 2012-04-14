/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.crypto;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Provides access to cryptology components.
 *
 * @since 1.3
 */
public interface CryptoHelper
{
    Cipher createCipher(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException;

    Signature createSignature(String algorithm) throws NoSuchAlgorithmException;

    SecureRandom createSecureRandom(String algorithm) throws NoSuchAlgorithmException;

    SecureRandom createSecureRandom();

    KeyStore createKeyStore(String type) throws KeyStoreException;

    KeyPairGenerator createKeyPairGenerator(String algorithm) throws NoSuchAlgorithmException;

    CertificateFactory createCertificateFactory(String type) throws CertificateException;

    KeyManagerFactory createKeyManagerFactory(String algorithm) throws NoSuchAlgorithmException;

    TrustManagerFactory createTrustManagerFactory(String algorithm) throws NoSuchAlgorithmException;

    MessageDigest createDigest(String algorithm) throws NoSuchAlgorithmException;
}

