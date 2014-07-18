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

/**
 * UT for {@link MavenCipherLegacyImpl}
 */
public class MavenCipherLegacyImplTest
    extends MavenCipherTestSupport
{
  private static final String passPhrase = "foofoo";

  private static final String plaintext = "my testing phrase";

  private static final String encrypted = "{CFUju8n8eKQHj8u0HI9uQMRmKQALtoXH7lY=}";

  public MavenCipherLegacyImplTest() {
    super(passPhrase, plaintext, encrypted, new MavenCipherLegacyImpl(new PasswordCipherMavenLegacyImpl(
        new CryptoHelperImpl())));
  }
}