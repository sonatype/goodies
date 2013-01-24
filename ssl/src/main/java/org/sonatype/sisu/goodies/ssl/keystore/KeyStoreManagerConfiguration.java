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
package org.sonatype.sisu.goodies.ssl.keystore;

import java.io.File;

import org.sonatype.sisu.goodies.common.Time;

/**
 * {@link KeyStoreManager} configuration.
 *
 * @since 1.5.2
 */
public interface KeyStoreManagerConfiguration

{

    File getBaseDir();

    String getKeyStoreType();

    String getKeyAlgorithm();

    int getKeyAlgorithmSize();

    Time getCertificateValidity();

    String getSignatureAlgorithm();

    String getKeyManagerAlgorithm();

    String getTrustManagerAlgorithm();

    String getPrivateKeyStorePassword();

    String getTrustedKeyStorePassword();

    String getPrivateKeyPassword();

}
