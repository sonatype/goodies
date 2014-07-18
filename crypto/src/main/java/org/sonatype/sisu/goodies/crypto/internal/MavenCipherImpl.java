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

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.crypto.MavenCipher;

/**
 * Implementation of {@link MavenCipher} compatible with plexus-cipher version [1.6,].
 * 
 * @since 1.10
 */
@Singleton
@Named(MavenCipher.CURRENT)
@ThreadSafe
public class MavenCipherImpl
    extends MavenCipherSupport
{
  @Inject
  public MavenCipherImpl(final PasswordCipherMavenImpl passwordCipher) {
    super(passwordCipher);
  }
}