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
package org.sonatype.sisu.goodies.crypto;

/**
 * Component for encrypting passwords. Uses format of Plexus Cipher.
 * 
 * @since 1.10
 */
public interface PasswordCipher
{
  /**
   * Encrypt the provided plaintext string using provided pass phrase.
   */
  String encrypt(String str, String passPhrase);

  /**
   * Decrypt the provided input string using provided pass phrase.
   */
  String decrypt(String str, String passPhrase);

  /**
   * Returns {@code true} if the passed in string is encrypted using {@link PasswordCipher} component.
   */
  boolean isPasswordCipher(String str);
}