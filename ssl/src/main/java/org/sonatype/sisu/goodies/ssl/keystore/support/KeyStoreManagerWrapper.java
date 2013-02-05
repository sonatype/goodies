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
package org.sonatype.sisu.goodies.ssl.keystore.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.util.Collection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.ssl.keystore.KeyStoreManager;
import org.sonatype.sisu.goodies.ssl.keystore.geronimo.KeystoreException;

/**
 * Wraps a {@link KeyStoreManager}.
 *
 * @since 1.5.2
 */
public class KeyStoreManagerWrapper
    extends ComponentSupport
    implements KeyStoreManager
{

    private final KeyStoreManager delegate;

    public KeyStoreManagerWrapper( final KeyStoreManager delegate )
    {
        this.delegate = checkNotNull( delegate );
    }

    @Override
    public TrustManager[] getTrustManagers()
        throws KeystoreException
    {
        return delegate.getTrustManagers();
    }

    @Override
    public KeyManager[] getKeyManagers()
        throws KeystoreException
    {
        return delegate.getKeyManagers();
    }

    @Override
    public void importTrustCertificate( final Certificate certificate, final String alias )
        throws KeystoreException
    {
        delegate.importTrustCertificate( certificate, alias );
    }

    @Override
    public void importTrustCertificate( final String certificateInPEM, final String alias )
        throws KeystoreException, CertificateParsingException
    {
        delegate.importTrustCertificate( certificateInPEM, alias );
    }

    @Override
    public Certificate getTrustedCertificate( final String alias )
        throws KeystoreException
    {
        return delegate.getTrustedCertificate( alias );
    }

    @Override
    public Collection<Certificate> getTrustedCertificates()
        throws KeystoreException
    {
        return delegate.getTrustedCertificates();
    }

    @Override
    public void removeTrustCertificate( final String alias )
        throws KeystoreException
    {
        delegate.removeTrustCertificate( alias );
    }

    @Override
    public void generateAndStoreKeyPair( final String commonName,
                                         final String organizationalUnit,
                                         final String organization,
                                         final String locality,
                                         final String state,
                                         final String country )
        throws KeystoreException
    {
        delegate.generateAndStoreKeyPair( commonName, organizationalUnit, organization, locality, state, country );
    }

    @Override
    public boolean isKeyPairInitialized()
    {
        return delegate.isKeyPairInitialized();
    }

    @Override
    public Certificate getCertificate()
        throws KeystoreException
    {
        return delegate.getCertificate();
    }

    @Override
    public void removePrivateKey()
        throws KeystoreException
    {
        delegate.removePrivateKey();
    }

    @Override
    public PrivateKey getPrivateKey()
        throws KeystoreException
    {
        return delegate.getPrivateKey();
    }

}
