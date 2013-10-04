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

package org.sonatype.sisu.goodies.crypto.internal;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.crypto.CryptoHelper;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link CryptoHelper} using {@link BouncyCastleProvider}.
 *
 * @since 1.3
 */
@Named
@Singleton
public class CryptoHelperImpl
    extends ComponentSupport
    implements CryptoHelper
{
  private final Provider provider;

  public CryptoHelperImpl() {
    this.provider = configureProvider();
  }

  /**
   * Configures the {@link BouncyCastleProvider} if its has not already been added.
   *
   * @return The {@link BouncyCastleProvider} instance.
   * @since 1.5
   */
  public static Provider configureProvider() {
    Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    if (provider == null) {
      provider = new BouncyCastleProvider();
      Security.addProvider(provider);
    }
    return provider;
  }

  public Provider getProvider() {
    return provider;
  }

  private void logFallback(final Throwable cause) {
    log.trace("Falling back to system selection due to: " + cause); // omit stack-trace
  }

  @Override
  public Cipher createCipher(final String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
    checkNotNull(transformation);
    Cipher obj;
    try {
      obj = Cipher.getInstance(transformation, getProvider());
    }
    catch (Exception e) {
      logFallback(e);
      obj = Cipher.getInstance(transformation);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created cipher: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public int getCipherMaxAllowedKeyLength(final String transformation) throws NoSuchAlgorithmException {
    return Cipher.getMaxAllowedKeyLength(transformation);
  }

  @Override
  public Signature createSignature(final String algorithm) throws NoSuchAlgorithmException {
    checkNotNull(algorithm);
    Signature obj;
    try {
      obj = Signature.getInstance(algorithm, getProvider());
    }
    catch (NoSuchAlgorithmException e) {
      logFallback(e);
      obj = Signature.getInstance(algorithm);
    }

    if (log.isTraceEnabled()) {
      log.trace("Created signature: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public SecureRandom createSecureRandom(final String algorithm) throws NoSuchAlgorithmException {
    checkNotNull(algorithm);
    SecureRandom obj;
    try {
      obj = SecureRandom.getInstance(algorithm, getProvider());
    }
    catch (NoSuchAlgorithmException e) {
      logFallback(e);
      obj = SecureRandom.getInstance(algorithm);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created secure-random: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public SecureRandom createSecureRandom() {
    SecureRandom obj = new SecureRandom();
    if (log.isTraceEnabled()) {
      log.trace("Created secure-random: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public KeyStore createKeyStore(final String type) throws KeyStoreException {
    checkNotNull(type);
    KeyStore obj;
    try {
      obj = KeyStore.getInstance(type, getProvider());
    }
    catch (KeyStoreException e) {
      logFallback(e);
      obj = KeyStore.getInstance(type);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created key-store: {} ({})", obj.getType(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public KeyPairGenerator createKeyPairGenerator(final String algorithm) throws NoSuchAlgorithmException {
    checkNotNull(algorithm);
    KeyPairGenerator obj;
    try {
      obj = KeyPairGenerator.getInstance(algorithm, getProvider());
    }
    catch (NoSuchAlgorithmException e) {
      logFallback(e);
      obj = KeyPairGenerator.getInstance(algorithm);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created key-pair-generator: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public CertificateFactory createCertificateFactory(final String type) throws CertificateException {
    checkNotNull(type);
    CertificateFactory obj;
    try {
      obj = CertificateFactory.getInstance(type, getProvider());
    }
    catch (CertificateException e) {
      logFallback(e);
      obj = CertificateFactory.getInstance(type);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created certificate-factory: {} ({})", obj.getType(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public KeyManagerFactory createKeyManagerFactory(final String algorithm) throws NoSuchAlgorithmException {
    checkNotNull(algorithm);
    KeyManagerFactory obj;
    try {
      obj = KeyManagerFactory.getInstance(algorithm, getProvider());
    }
    catch (NoSuchAlgorithmException e) {
      logFallback(e);
      obj = KeyManagerFactory.getInstance(algorithm);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created key-manager-factory: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public TrustManagerFactory createTrustManagerFactory(final String algorithm) throws NoSuchAlgorithmException {
    checkNotNull(algorithm);
    TrustManagerFactory obj;
    try {
      obj = TrustManagerFactory.getInstance(algorithm, getProvider());
    }
    catch (NoSuchAlgorithmException e) {
      logFallback(e);
      obj = TrustManagerFactory.getInstance(algorithm);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created trust-manager-factory: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  @Override
  public MessageDigest createDigest(final String algorithm) throws NoSuchAlgorithmException {
    checkNotNull(algorithm);
    MessageDigest obj;
    try {
      obj = MessageDigest.getInstance(algorithm, getProvider());
    }
    catch (NoSuchAlgorithmException e) {
      logFallback(e);
      obj = MessageDigest.getInstance(algorithm);
    }
    if (log.isTraceEnabled()) {
      log.trace("Created message-digest: {} ({})", obj.getAlgorithm(), obj.getProvider().getName());
    }
    return obj;
  }

  /**
   * @since 1.8
   */
  @Override
  public PGPSignatureList readPGPSignatures(final InputStream inputStream) throws IOException, PGPException {
    checkNotNull(inputStream, "inputStream");

    InputStream in = PGPUtil.getDecoderStream(inputStream);

    PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(in);

    Object pgpObject = pgpObjectFactory.nextObject();

    if (pgpObject == null) {
      throw new PGPException("No pgp objects found in the input stream");
    }

    if (pgpObject instanceof PGPCompressedData) {
      pgpObjectFactory = new PGPObjectFactory(((PGPCompressedData) pgpObject).getDataStream());

      return (PGPSignatureList) pgpObjectFactory.nextObject();
    }
    else {
      return (PGPSignatureList) pgpObject;
    }
  }

  /**
   * @since 1.8
   */
  @Override
  public boolean verifyPGPSignature(final InputStream inputStream,
                                    final PGPSignature signature,
                                    final PGPPublicKey pubKey)
      throws IOException, PGPException, SignatureException
  {
    signature.init(new JcaPGPContentVerifierBuilderProvider().setProvider(provider), pubKey);
    byte[] buffer = new byte[10240];
    int bytes;
    while ((bytes = inputStream.read(buffer)) != -1) {
      signature.update(buffer, 0, bytes);
    }
    return signature.verify();
  }

}

