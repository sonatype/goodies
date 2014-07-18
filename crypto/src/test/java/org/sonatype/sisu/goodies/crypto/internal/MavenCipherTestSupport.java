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

import java.security.Security;

import org.sonatype.sisu.goodies.crypto.MavenCipher;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import static com.google.common.base.Preconditions.checkNotNull;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * UT support for {@link MavenCipher} implementations containing simple set of excersises.
 */
public abstract class MavenCipherTestSupport
    extends TestSupport
{
  protected final String passPhrase;

  protected final String plaintext;

  protected final String encrypted;

  protected final MavenCipher testSubject;

  protected MavenCipherTestSupport(final String passPhrase, final String plaintext, final String encrypted,
      final MavenCipher testSubject)
  {
    this.passPhrase = checkNotNull(passPhrase);
    this.plaintext = checkNotNull(plaintext);
    this.encrypted = checkNotNull(encrypted);
    this.testSubject = checkNotNull(testSubject);
  }

  @Before
  public void prepare() {
    Security.addProvider(new BouncyCastleProvider());
  }

  @After
  public void cleanup() {
    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
  }

  @Test
  public void payloadDetection() {
    assertThat(testSubject.isPasswordCipher(plaintext), is(false));
    assertThat(testSubject.isPasswordCipher(""), is(false));
    assertThat(testSubject.isPasswordCipher("{}"), is(false));
    assertThat(testSubject.isPasswordCipher(null), is(false));
    assertThat(testSubject.isPasswordCipher(encrypted), is(true));
    assertThat(testSubject.isPasswordCipher("{ }"), is(true));
  }

  @Test
  public void encrypt() throws Exception {
    String enc = testSubject.encrypt(plaintext, passPhrase);
    assertThat(enc, notNullValue());
  }

  @Test
  public void decrypt() throws Exception {
    String dec = testSubject.decrypt(encrypted, passPhrase);
    assertThat(dec, equalTo(plaintext));
  }

  @Test(expected = IllegalArgumentException.class)
  public void decryptCorruptedMissingEnd() throws Exception {
    testSubject.decrypt("{CFUju8n8eKQHj8u0HI9uQMRm", passPhrase);
  }

  @Test(expected = NullPointerException.class)
  public void decryptNull() throws Exception {
    testSubject.decrypt(null, passPhrase);
  }

  @Test(expected = NullPointerException.class)
  public void decryptNullPassPhrase() throws Exception {
    testSubject.decrypt(encrypted, null);
  }

  @Test
  public void roundTrip() throws Exception {
    String dec = testSubject.decrypt(testSubject.encrypt(plaintext, passPhrase), passPhrase);
    assertThat(dec, equalTo(plaintext));
  }
}