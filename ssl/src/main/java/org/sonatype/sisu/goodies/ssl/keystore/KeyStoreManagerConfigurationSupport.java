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
package org.sonatype.sisu.goodies.ssl.keystore;

import java.io.File;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.Time;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mutable {@link KeyStoreManagerConfiguration}.
 *
 * @since 1.6
 */
public class KeyStoreManagerConfigurationSupport
    extends ComponentSupport
    implements KeyStoreManagerConfiguration
{
  private static final String DEFAULT = "DEFAULT";

  private File baseDir;

  private String fileNamesPrefix;

  private String keyStoreType = "JKS";

  private String keyAlgorithm = "RSA";

  private int keyAlgorithmSize = 2048;

  private Time certificateValidity = Time.days(36500);

  private String signatureAlgorithm = "SHA1WITHRSA";

  private String keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

  private String trustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

  private char[] privateKeyStorePassword;

  private char[] trustedKeyStorePassword;

  private char[] privateKeyPassword;

  public void setBaseDir(final File baseDir) {
    this.baseDir = checkNotNull(baseDir);
    log.debug("Basedir: {}", baseDir);
  }

  @Override
  public File getBaseDir() {
    return baseDir;
  }

  public void setFileNamesPrefix(final String fileNamesPrefix) {
    this.fileNamesPrefix = fileNamesPrefix;
  }

  @Override
  public String getFileNamesPrefix() {
    return fileNamesPrefix;
  }

  public void setKeyStoreType(final String keyStoreType) {
    this.keyStoreType = checkNotNull(keyStoreType);
    log.debug("Key-store type: {}", keyStoreType);
  }

  @Override
  public String getKeyStoreType() {
    return keyStoreType;
  }

  public void setKeyAlgorithm(final String keyAlgorithm) {
    this.keyAlgorithm = checkNotNull(keyAlgorithm);
    log.debug("Key algorithm: {}", keyAlgorithm);
  }

  @Override
  public String getKeyAlgorithm() {
    return keyAlgorithm;
  }

  public void setKeyAlgorithmSize(final int keyAlgorithmSize) {
    this.keyAlgorithmSize = keyAlgorithmSize;
    log.debug("Key algorithm size: {}", keyAlgorithmSize);
  }

  @Override
  public int getKeyAlgorithmSize() {
    return keyAlgorithmSize;
  }

  public void setCertificateValidity(final Time certificateValidity) {
    this.certificateValidity = certificateValidity;
    log.debug("Certificate validity: {}", certificateValidity);
  }

  @Override
  public Time getCertificateValidity() {
    return certificateValidity;
  }

  public void setSignatureAlgorithm(final String signatureAlgorithm) {
    this.signatureAlgorithm = checkNotNull(signatureAlgorithm);
    log.debug("Signature algorithm: {}", signatureAlgorithm);
  }

  @Override
  public String getSignatureAlgorithm() {
    return signatureAlgorithm;
  }

  public void setKeyManagerAlgorithm(String keyManagerAlgorithm) {
    checkNotNull(keyManagerAlgorithm);
    if (keyManagerAlgorithm.equals(DEFAULT)) {
      this.keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
    }
    else {
      this.keyManagerAlgorithm = keyManagerAlgorithm;
    }
    log.debug("Key-manager algorithm: {}", this.keyManagerAlgorithm);
  }

  @Override
  public String getKeyManagerAlgorithm() {
    return keyManagerAlgorithm;
  }

  public void setTrustManagerAlgorithm(final String trustManagerAlgorithm) {
    checkNotNull(trustManagerAlgorithm);
    if (trustManagerAlgorithm.equals(DEFAULT)) {
      this.trustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    }
    else {
      this.trustManagerAlgorithm = trustManagerAlgorithm;
    }
    log.debug("Trust-manager algorithm: {}", this.trustManagerAlgorithm);
  }

  @Override
  public String getTrustManagerAlgorithm() {
    return trustManagerAlgorithm;
  }

  public void setPrivateKeyStorePassword(final char[] password) {
    this.privateKeyStorePassword = password;
  }

  @Override
  public char[] getPrivateKeyStorePassword() {
    return privateKeyStorePassword;
  }

  public void setTrustedKeyStorePassword(final char[] password) {
    this.trustedKeyStorePassword = password;
  }

  @Override
  public char[] getTrustedKeyStorePassword() {
    return trustedKeyStorePassword;
  }

  public void setPrivateKeyPassword(final char[] password) {
    this.privateKeyPassword = password;
  }

  @Override
  public char[] getPrivateKeyPassword() {
    return privateKeyPassword;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "baseDir=" + baseDir +
        ", keyStoreType='" + keyStoreType + '\'' +
        ", keyAlgorithm='" + keyAlgorithm + '\'' +
        ", keyAlgorithmSize=" + keyAlgorithmSize +
        ", certificateValidity=" + certificateValidity +
        ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
        ", keyManagerAlgorithm='" + keyManagerAlgorithm + '\'' +
        ", trustManagerAlgorithm='" + trustManagerAlgorithm + '\'' +
        '}';
  }
}
