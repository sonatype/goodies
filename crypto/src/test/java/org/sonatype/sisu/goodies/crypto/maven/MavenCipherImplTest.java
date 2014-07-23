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

import org.sonatype.sisu.goodies.crypto.internal.CryptoHelperImpl;
import org.sonatype.sisu.goodies.crypto.maven.PasswordCipherMavenImpl;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * UT for {@link MavenCipher} with {@link PasswordCipherMavenImpl}
 */
public class MavenCipherImplTest
    extends MavenCipherTestSupport
{
  private static final String passPhrase = "foofoo";

  private static final String plaintext = "my testing phrase";

  private static final String encrypted = "{5FjvnZvhNDMHHnxXoPu1a0WcgZzaArKRCnGBnsA83R7rYQHKGFrprtAM4Qyr4diV}";

  public MavenCipherImplTest() {
    super(passPhrase, plaintext, encrypted, new MavenCipher(new PasswordCipherMavenImpl(new CryptoHelperImpl())));
  }

  /**
   * This is a "master password" string created using Maven 3.0.4 on CLI as described here:
   * http://maven.apache.org/guides/mini/guide-encryption.html#How_to_create_a_master_password
   */
  @Test
  public void masterPasswordCreatedWithMaven304Cli() {
    String passPhrase = "settings.security";
    String plaintext = "123321";
    String encrypted = "{KW5k/vol4xMHusz6ikZqdj0t4YRClp4/5Dsb30+M9R0=}";
    assertThat(testSubject.decrypt(encrypted, passPhrase), equalTo(plaintext));
  }

  /**
   * This is a "master password" string created using Maven 3.2.2 on CLI as described here:
   * http://maven.apache.org/guides/mini/guide-encryption.html#How_to_create_a_master_password
   */
  @Test
  public void masterPasswordCreatedWithMaven322Cli() {
    String passPhrase = "settings.security";
    String plaintext = "123321";
    String encrypted = "{eO8Yc66/I/IHaeg4CoF+/o5bwS5IIyfWcgsYhS0s9W8=}";
    assertThat(testSubject.decrypt(encrypted, passPhrase), equalTo(plaintext));
  }
}