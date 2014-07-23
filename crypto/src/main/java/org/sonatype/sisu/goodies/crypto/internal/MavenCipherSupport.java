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

import javax.annotation.Nullable;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.crypto.MavenCipher;
import org.sonatype.sisu.goodies.crypto.PasswordCipher;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support class for {@link MavenCipher} implementations.
 * 
 * @since 1.10
 */
public abstract class MavenCipherSupport
    extends ComponentSupport
    implements MavenCipher
{
  private static final char SHIELD_BEGIN = '{';

  private static final char SHIELD_END = '}';

  private final PasswordCipher passwordCipher;

  protected MavenCipherSupport(final PasswordCipher passwordCipher) {
    this.passwordCipher = checkNotNull(passwordCipher);
  }

  @Override
  public String encrypt(final String str, final String passPhrase) {
    checkNotNull(str);
    checkNotNull(passPhrase);
    return SHIELD_BEGIN + doEncrypt(str, passPhrase) + SHIELD_END;
  }

  protected String doEncrypt(final String str, final String passPhrase) {
    return new String(passwordCipher.encrypt(str.getBytes(Charsets.UTF_8), passPhrase), Charsets.UTF_8);
  }

  @Override
  public String decrypt(final String str, final String passPhrase) {
    checkNotNull(str);
    checkNotNull(passPhrase);
    String payload = peel(str);
    checkArgument(payload != null, "Input string is not a password cipher");
    return doDecrypt(payload, passPhrase);
  }

  protected String doDecrypt(final String str, final String passPhrase) {
    return new String(passwordCipher.decrypt(str.getBytes(Charsets.UTF_8), passPhrase), Charsets.UTF_8);
  }

  @Override
  public boolean isPasswordCipher(final String str) {
    return peel(str) != null;
  }

  /**
   * Peels of the start and stop "shield" braces from payload if possible, otherwise returns {@code null} signaling that
   * input is invalid.
   */
  @Nullable
  protected String peel(final String str) {
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