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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.sonatype.sisu.goodies.ssl.keystore.internal.DigesterUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple utility methods when dealing with certificates.
 * <p/>
 * In order to use the methods decodePEMFormattedCertificate or serializeCertificateInPEM, the BouncyCastleProvider
 * must
 * be registered. <BR/><BR/>
 * <pre>
 * <code>
 *    if( Security.getProvider( "BC" ) == null )
 *    {
 *        Security.addProvider( new BouncyCastleProvider() );
 *    }
 * </code>
 * </pre>
 *
 * @since 1.6
 */
public final class CertificateUtil
{

  @NonNls
  private static final Logger LOG = LoggerFactory.getLogger(CertificateUtil.class);

  /**
   * Private constructor, this class only has static methods.
   */
  private CertificateUtil() {
  }

  public static X509Certificate generateCertificate(PublicKey publicKey, PrivateKey privateKey, String algorithm,
                                                    int validDays, String commonName, String orgUnit,
                                                    String organization, String locality, String state,
                                                    String country)
      throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, CertificateEncodingException
  {
    X509V3CertificateGenerator certificateGenerator = new X509V3CertificateGenerator();
    Vector<ASN1ObjectIdentifier> order = new Vector<ASN1ObjectIdentifier>();
    Hashtable<ASN1ObjectIdentifier, String> attributeMap = new Hashtable<ASN1ObjectIdentifier, String>();

    if (commonName != null) {
      attributeMap.put(X509Principal.CN, commonName);
      order.add(X509Principal.CN);
    }

    if (orgUnit != null) {
      attributeMap.put(X509Principal.OU, orgUnit);
      order.add(X509Principal.OU);
    }

    if (organization != null) {
      attributeMap.put(X509Principal.O, organization);
      order.add(X509Principal.O);
    }

    if (locality != null) {
      attributeMap.put(X509Principal.L, locality);
      order.add(X509Principal.L);
    }

    if (state != null) {
      attributeMap.put(X509Principal.ST, state);
      order.add(X509Principal.ST);
    }

    if (country != null) {
      attributeMap.put(X509Principal.C, country);
      order.add(X509Principal.C);
    }

    X509Principal issuerDN = new X509Principal(order, attributeMap);

    // validity
    long now = System.currentTimeMillis();
    long expire = now + (long) validDays * 24 * 60 * 60 * 1000;

    certificateGenerator.setNotBefore(new Date(now));
    certificateGenerator.setNotAfter(new Date(expire));
    certificateGenerator.setIssuerDN(issuerDN);
    certificateGenerator.setSubjectDN(issuerDN);
    certificateGenerator.setPublicKey(publicKey);
    certificateGenerator.setSignatureAlgorithm(algorithm);
    certificateGenerator.setSerialNumber(new BigInteger(String.valueOf(now)));

    // make certificate
    return certificateGenerator.generate(privateKey);
  }

  /**
   * Serialize a certificate into a PEM formatted String.
   *
   * @param certificate the certificate to be serialized.
   * @return the certificate in PEM format
   * @throws java.io.IOException thrown if the certificate cannot be converted into the PEM format.
   */
  public static String serializeCertificateInPEM(Certificate certificate)
      throws IOException
  {
    StringWriter stringWriter = new StringWriter();
    PEMWriter pemWriter = new PEMWriter(stringWriter);
    pemWriter.writeObject(certificate);
    pemWriter.close();

    return stringWriter.toString();
  }

  /**
   * Decodes a PEM formatted certificate.
   *
   * @param pemFormattedCertificate text to be decoded as a PEM certificate.
   * @return the Certificate decoded from the input text.
   * @throws java.security.cert.CertificateParsingException
   *          thrown if the PEM formatted string cannot be parsed into a Certificate.
   */
  public static Certificate decodePEMFormattedCertificate(String pemFormattedCertificate)
      throws CertificateParsingException
  {
    LOG.trace("Parsing PEM formatted certificate string:\n{}", pemFormattedCertificate);

    // make sure we have something to parse
    if (pemFormattedCertificate != null) {
      StringReader stringReader = new StringReader(pemFormattedCertificate);
      PEMParser pemReader = new PEMParser(stringReader);
      try {
        Object object = pemReader.readObject();
        LOG.trace("Object found while paring PEM formatted string: {}", object);

        // verify the object is a cert
        if (object instanceof X509CertificateHolder) {
          X509CertificateHolder holder = (X509CertificateHolder)object;
          JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
          return converter.getCertificate(holder);
        }
      }
      catch (IOException e) {
        throw new CertificateParsingException(
            "Failed to parse valid certificate from expected PEM formatted certificate:\n"
                + pemFormattedCertificate, e);
      }
      catch (CertificateException e) {
        throw new CertificateParsingException(
            "Failed to parse valid certificate from expected PEM formatted certificate:\n"
                + pemFormattedCertificate, e);
      }
    }

    // cert was not a valid object
    throw new CertificateParsingException(
        "Failed to parse valid certificate from expected PEM formatted certificate:\n" + pemFormattedCertificate);
  }

  /**
   * Calculates the SHA1 of a Certificate.
   */
  public static String calculateSha1(final Certificate certificate)
      throws CertificateEncodingException
  {
    checkNotNull(certificate);
    return DigesterUtils.getSha1Digest(certificate.getEncoded()).toUpperCase();
  }

  /**
   * Calculates the SHA1 of a Certificate and formats for readability, such as <code>64:C4:44:A9:02:F7:F0:02:16:AA:C3:43:0B:BF:ED:44:C8:81:87:CD<code/>.
   */
  public static String calculateFingerprint(final Certificate certificate)
      throws CertificateEncodingException
  {
    String sha1Hash = calculateSha1(certificate);
    return encode(sha1Hash, ':', 2);
  }

  private static String encode(final String input, final char separator, final int delay) {
    StringBuilder buff = new StringBuilder();

    int i = 0;
    for (char c : input.toCharArray()) {
      if (i != 0 && i % delay == 0) {
        buff.append(separator);
      }
      buff.append(c);
      i++;
    }

    return buff.toString();
  }

}
