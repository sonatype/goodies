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

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.After;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * UT for {@link DefaultPasswordCipher}
 */
public class DefaultPasswordCipherTest
    extends TestSupport
{
  private String passPhrase = "foofoo";

  private String plaintext = "my testing phrase";

  private String encrypted = "{CFUju8n8eKQHj8u0HI9uQMRmKQALtoXH7lY=}";

  private DefaultPasswordCipher testSubject;

  @Before
  public void prepare() {
    Security.addProvider(new BouncyCastleProvider());
    this.testSubject = new DefaultPasswordCipher(new CryptoHelperImpl(), "PBEWithSHAAnd128BitRC4", 23);
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