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

package org.sonatype.sisu.goodies.crypto.maven;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.sonatype.sisu.goodies.crypto.CryptoHelper;
import org.sonatype.sisu.goodies.crypto.PasswordCipher;

import com.google.common.base.Throwables;
import org.bouncycastle.util.encoders.Base64Encoder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Maven legacy impl of {@link PasswordCipher} compatible with encryption used by plexus-cipher versions [1.0,1.5].
 *
 * @since 1.10
 */
@ThreadSafe
public class PasswordCipherMavenLegacyImpl
    implements PasswordCipher
{
  private static final String DEFAULT_ALGORITHM = "PBEWithSHAAnd128BitRC4";

  private static final int DEFAULT_ITERATION_COUNT = 23;

  private static final int DEFAULT_SALT_SIZE = 8;

  private final String algorithm;

  private final int iterationCount;

  private final int saltSize;

  private final CryptoHelper cryptoHelper;

  private final Base64Encoder base64Encoder;

  private final SecureRandom secureRandom;

  /**
   * Creates instance using "defaults" (from Plexus Cipher).
   */
  public PasswordCipherMavenLegacyImpl(final CryptoHelper cryptoHelper) {
    this(cryptoHelper, DEFAULT_ALGORITHM, DEFAULT_ITERATION_COUNT, DEFAULT_SALT_SIZE);
  }

  /**
   * Allows customization of algorithm, iteration count and salt size as DefaultPlexusCipher did.
   */
  public PasswordCipherMavenLegacyImpl(final CryptoHelper cryptoHelper,
                                       final String algorithm,
                                       final int iterationCount,
                                       final int saltSize)
  {
    this.cryptoHelper = checkNotNull(cryptoHelper);
    this.algorithm = checkNotNull(algorithm);
    this.iterationCount = iterationCount;
    this.saltSize = saltSize;
    this.base64Encoder = new Base64Encoder();
    this.secureRandom = cryptoHelper.createSecureRandom();
    this.secureRandom.setSeed(System.nanoTime());
  }

  @Override
  public byte[] encrypt(final byte[] payload, final String passPhrase) {
    checkNotNull(payload);
    checkNotNull(passPhrase);
    try {
      byte[] salt = new byte[saltSize];
      secureRandom.nextBytes(salt);
      byte[] enc = createCipher(passPhrase, salt, Cipher.ENCRYPT_MODE).doFinal(payload);
      byte saltLen = (byte) (salt.length & 0x00ff);
      int encLen = enc.length;
      byte[] res = new byte[salt.length + encLen + 1];
      res[0] = saltLen;
      System.arraycopy(salt, 0, res, 1, saltLen);
      System.arraycopy(enc, 0, res, saltLen + 1, encLen);
      ByteArrayOutputStream bout = new ByteArrayOutputStream(res.length * 2);
      base64Encoder.encode(res, 0, res.length, bout);
      return bout.toByteArray();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public byte[] decrypt(final byte[] payload, final String passPhrase) {
    checkNotNull(payload);
    checkNotNull(passPhrase);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      base64Encoder.decode(payload, 0, payload.length, baos);
      byte[] res = baos.toByteArray();
      int saltLen = res[0] & 0x00ff;
      checkArgument(saltLen == saltSize, "Encrypted string corrupted (salt size)");
      checkArgument(res.length >= (saltLen + 2), "Encrypted string corrupted (payload size)");
      byte[] salt = new byte[saltLen];
      System.arraycopy(res, 1, salt, 0, saltLen);
      int decLen = res.length - saltLen - 1;
      checkArgument(decLen >= 1, "Encrypted string corrupted (payload segment)");
      byte[] dec = new byte[decLen];
      System.arraycopy(res, saltLen + 1, dec, 0, decLen);
      Cipher cipher = createCipher(passPhrase, salt, Cipher.DECRYPT_MODE);
      return cipher.doFinal(dec);
    }
    catch (IOException e) {
      throw new IllegalArgumentException("Invalid payload (base64 problem)", e);
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  // ==

  private Cipher createCipher(final String passPhrase, final byte[] salt, final int mode) throws Exception {
    Cipher cipher = cryptoHelper.createCipher(algorithm);
    KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
    SecretKey key = cryptoHelper.createSecretKeyFactory(algorithm).generateSecret(keySpec);
    PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
    cipher.init(mode, key, paramSpec);
    return cipher;
  }
}