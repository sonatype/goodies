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
package org.sonatype.sisu.goodies.crypto.internal;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.crypto.CryptoHelper;
import org.sonatype.sisu.goodies.crypto.PasswordCipher;

import com.google.common.base.Throwables;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.fest.util.Strings;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of {@link PasswordCipher}.
 * 
 * @since 3.0
 */
@Singleton
@Named
@ThreadSafe
public class DefaultPasswordCipher
    extends ComponentSupport
    implements PasswordCipher
{
  private static final char ENCRYPTED_STRING_DECORATION_START = '{';

  private static final char ENCRYPTED_STRING_DECORATION_STOP = '}';

  private static final String STRING_ENCODING = "UTF8";

  private static final int SALT_SIZE = 8;

  private final String algorithm;

  private final int iterationCount;

  private final CryptoHelper cryptoHelper;

  private final Base64Encoder base64Encoder;

  @Inject
  public DefaultPasswordCipher(final CryptoHelper cryptoHelper,
      final @Named("${passwordCipher.algorithm:-PBEWithSHAAnd128BitRC4}") String algorithm,
      final @Named("${passwordCipher.iterationCount:-23}") int iterationCount)
  {
    this.cryptoHelper = checkNotNull(cryptoHelper);
    this.algorithm = checkNotNull(algorithm);
    this.iterationCount = iterationCount;
    this.base64Encoder = new Base64Encoder();
  }

  @Override
  public String encrypt(final String str, final String passPhrase) {
    checkNotNull(str);
    checkNotNull(passPhrase);
    try {
      SecureRandom secureRandom = cryptoHelper.createSecureRandom();
      secureRandom.setSeed(System.nanoTime());
      byte[] salt = secureRandom.generateSeed(SALT_SIZE);
      Cipher cipher = createCipher(passPhrase, salt, true);
      byte[] utf8 = str.getBytes(STRING_ENCODING);
      byte[] enc = cipher.doFinal(utf8);
      byte saltLen = (byte) (salt.length & 0x00ff);
      int encLen = enc.length;
      byte[] res = new byte[salt.length + encLen + 1];
      res[0] = saltLen;
      System.arraycopy(salt, 0, res, 1, saltLen);
      System.arraycopy(enc, 0, res, saltLen + 1, encLen);
      ByteArrayOutputStream bout = new ByteArrayOutputStream(res.length * 2);
      base64Encoder.encode(res, 0, res.length, bout);
      return ENCRYPTED_STRING_DECORATION_START + bout.toString(STRING_ENCODING) + ENCRYPTED_STRING_DECORATION_STOP;
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public String decrypt(final String str, final String passPhrase) {
    checkNotNull(str);
    checkNotNull(passPhrase);

    String payload = peel(str);
    checkArgument(payload != null, "Input string is not a password cipher");
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      base64Encoder.decode(payload, baos);
      byte[] res = baos.toByteArray();
      int saltLen = res[0] & 0x00ff;
      checkArgument(saltLen == SALT_SIZE, "Encrypted string corrupted (salt size)");
      checkArgument(res.length >= (saltLen + 2), "Encrypted string corrupted (payload size)");
      byte[] salt = new byte[saltLen];
      System.arraycopy(res, 1, salt, 0, saltLen);
      int decLen = res.length - saltLen - 1;
      checkArgument(decLen >= 1, "Encrypted string corrupted (payload segment)");
      byte[] dec = new byte[decLen];
      System.arraycopy(res, saltLen + 1, dec, 0, decLen);
      Cipher cipher = createCipher(passPhrase, salt, false);
      byte[] utf8 = cipher.doFinal(dec);
      return new String(utf8, "UTF8");
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public boolean isPasswordCipher(final String str) {
    return peel(str) != null;
  }

  // ==

  private Cipher createCipher(final String passPhrase, final byte[] salt, final boolean encrypt) {
    int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
    try {
      Cipher cipher = cryptoHelper.createCipher(algorithm);
      PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
      KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
      SecretKey key = SecretKeyFactory.getInstance(algorithm, ((CryptoHelperImpl) cryptoHelper).getProvider())
          .generateSecret(keySpec);
      cipher.init(mode, key, paramSpec);
      return cipher;
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Peels of the start and stop braces from payload if possible, otherwise returns {@code null} signalling that input
   * is invalid.
   */
  private String peel(final String str) {
    if (Strings.isNullOrEmpty(str)) {
      return null;
    }
    int start = str.indexOf(ENCRYPTED_STRING_DECORATION_START);
    int stop = str.indexOf(ENCRYPTED_STRING_DECORATION_STOP);
    if (start != -1 && stop != -1 && stop > start + 1) {
      return str.substring(start + 1, stop);
    }
    return null;
  }
}