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

import java.security.SecureRandom;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.crypto.CryptoHelper;
import org.sonatype.sisu.goodies.crypto.RandomBytesGenerator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link RandomBytesGenerator} implementation.
 *
 * @since 1.3
 */
@Named
public class RandomBytesGeneratorImpl
    extends ComponentSupport
    implements RandomBytesGenerator
{
  private final SecureRandom random;

  @Inject
  public RandomBytesGeneratorImpl(final CryptoHelper crypto) {
    this.random = checkNotNull(crypto).createSecureRandom();
  }

  public byte[] generate(final int size) {
    checkArgument(size > 0);
    byte[] bytes = new byte[size];
    random.nextBytes(bytes);
    return bytes;
  }
}
