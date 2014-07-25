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

import javax.annotation.Nullable;

import org.sonatype.sisu.goodies.crypto.PasswordCipher;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link PasswordCipher} wrapper that uses Apache Maven format (aka Plexus Cipher).
 *
 * Is meant to be a drop-in replacement for plexus-cipher. Compatibility with given version of Plexus Cipher depends
 * on which {@link PasswordCipher} is used with this class.
 *
 * @see PasswordCipherMavenImpl
 * @see PasswordCipherMavenLegacyImpl
 * 
 * @since 1.10
 */
public class MavenCipher
{
  private static final char SHIELD_BEGIN = '{';

  private static final char SHIELD_END = '}';

  private final PasswordCipher passwordCipher;

  public MavenCipher(final PasswordCipher passwordCipher) {
    this.passwordCipher = checkNotNull(passwordCipher);
  }

  public String encrypt(final String str, final String passPhrase) {
    checkNotNull(str);
    checkNotNull(passPhrase);
    return SHIELD_BEGIN + doEncrypt(str, passPhrase) + SHIELD_END;
  }

  private String doEncrypt(final String str, final String passPhrase) {
    return new String(passwordCipher.encrypt(str.getBytes(Charsets.UTF_8), passPhrase), Charsets.UTF_8);
  }

  public String decrypt(final String str, final String passPhrase) {
    checkNotNull(str);
    checkNotNull(passPhrase);
    String payload = peel(str);
    checkArgument(payload != null, "Input string is not a password cipher");
    return doDecrypt(payload, passPhrase);
  }

  private String doDecrypt(final String str, final String passPhrase) {
    return new String(passwordCipher.decrypt(str.getBytes(Charsets.UTF_8), passPhrase), Charsets.UTF_8);
  }

  public boolean isPasswordCipher(final String str) {
    return peel(str) != null;
  }

  /**
   * Peels of the start and stop "shield" braces from payload if possible, otherwise returns {@code null} signaling that
   * input is invalid.
   */
  @Nullable
  private String peel(final String str) {
    if (Strings.isNullOrEmpty(str)) {
      return null;
    }
    int start = str.indexOf(SHIELD_BEGIN);
    int stop = str.indexOf(SHIELD_END);
    if (start != -1 && stop != -1 && stop > start + 1) {
      return str.substring(start + 1, stop);
    }
    return null;
  }
}