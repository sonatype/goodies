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

import java.security.Security;
import java.security.cert.Certificate;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests for {@link CertificateUtil}.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class CertificateUtilTest
    extends TestSupport
{

  public static final String NL = System.getProperty("line.separator");

  // Need to use platform NL here for compatibility
  private final String CERT_IN_PEM =
      "-----BEGIN CERTIFICATE-----" + NL
          + "MIIByzCCAXUCBgE0OsUqMjANBgkqhkiG9w0BAQUFADBtMRYwFAYDVQQDEw10byBi" + NL
          + "ZSBjaGFuZ2VkMQ8wDQYDVQQLEwZjaGFuZ2UxDzANBgNVBAoTBmNoYW5nZTEPMA0G" + NL
          + "A1UEBxMGY2hhbmdlMQ8wDQYDVQQIEwZjaGFuZ2UxDzANBgNVBAYTBmNoYW5nZTAg" + NL
          + "Fw0xMTEyMTQwNDEyMDdaGA8yMTExMTEyMDA0MTIwN1owbTEWMBQGA1UEAxMNdG8g" + NL
          + "YmUgY2hhbmdlZDEPMA0GA1UECxMGY2hhbmdlMQ8wDQYDVQQKEwZjaGFuZ2UxDzAN" + NL
          + "BgNVBAcTBmNoYW5nZTEPMA0GA1UECBMGY2hhbmdlMQ8wDQYDVQQGEwZjaGFuZ2Uw" + NL
          + "XDANBgkqhkiG9w0BAQEFAANLADBIAkEAtyZDEbRZ9snDlCQbKerKAGGMHXIWF1t2" + NL
          + "6SBEAuC6krlujo5vMQsE/0Qp0jePjf9IKj8dR5RcXDKNi4mITY/Y4wIDAQABMA0G" + NL
          + "CSqGSIb3DQEBBQUAA0EAjX5DHXWkFxVWuvymp/2VUkcs8/PV1URpjpnVRL22GbXU" + NL
          + "UTlNxF8vcC+LMpLCaAk3OLezSwYkpptRFK/x3EWq7g==" + NL
          + "-----END CERTIFICATE-----";

  private static final String SHA_CERT_FINGERPRINT = "64:C4:44:A9:02:F7:F0:02:16:AA:C3:43:0B:BF:ED:44:C8:81:87:CD";

  @Before
  public void registerBouncyCastle() {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  /**
   * Tests a Certificate can be decoded then serialized and end up with the same result.
   */
  @Test
  public void testMarshalPEMFormat()
      throws Exception
  {
    Certificate certificate = CertificateUtil.decodePEMFormattedCertificate(CERT_IN_PEM);
    assertThat(CertificateUtil.serializeCertificateInPEM(certificate).trim(), equalTo(CERT_IN_PEM));
  }

  /**
   * Tests calculating a fingerprint for a Certificate.
   */
  @Test
  public void testCalculateFingerPrint()
      throws Exception
  {
    Certificate certificate = CertificateUtil.decodePEMFormattedCertificate(CERT_IN_PEM);
    String actualFingerPrint = CertificateUtil.calculateFingerprint(certificate);
    assertThat(actualFingerPrint, equalTo(SHA_CERT_FINGERPRINT));
  }
}
