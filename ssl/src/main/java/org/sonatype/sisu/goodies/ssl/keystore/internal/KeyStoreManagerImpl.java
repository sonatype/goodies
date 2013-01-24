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
package org.sonatype.sisu.goodies.ssl.keystore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.jetbrains.annotations.NonNls;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.TestAccessible;
import org.sonatype.sisu.goodies.crypto.CryptoHelper;
import org.sonatype.sisu.goodies.ssl.keystore.CertificateUtil;
import org.sonatype.sisu.goodies.ssl.keystore.KeyStoreManager;
import org.sonatype.sisu.goodies.ssl.keystore.KeyStoreManagerConfiguration;
import org.sonatype.sisu.goodies.ssl.keystore.internal.geronimo.FileKeystoreInstance;
import org.sonatype.sisu.goodies.ssl.keystore.geronimo.KeystoreException;
import org.sonatype.sisu.goodies.ssl.keystore.internal.geronimo.KeystoreInstance;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * An implementation of a {@link KeyStoreManager} that stores the trusted certificates and
 * public/private key used for authentication in different key-stores.
 * <p/>
 * Current key-store implementation appears to NOT to have case-sensitive alias support (ie. "myKey" = "mykey").
 *
 * @since 1.5.2
 */
public class KeyStoreManagerImpl
    extends ComponentSupport
    implements KeyStoreManager
{

    @NonNls
    private static final String PRIVATE_KEY_STORE_NAME = "private.ks";

    @NonNls
    private static final String TRUSTED_KEY_STORE_NAME = "trusted.ks";

    @NonNls
    private static final String DEFAULT00_KEY_ALIAS = "__default00";

    @TestAccessible
    @NonNls
    static final String PRIVATE_KEY_ALIAS = "identity";

    private final CryptoHelper crypto;

    private final KeyStoreManagerConfiguration config;

    private final KeystoreInstance privateKeyStore;

    private final KeystoreInstance trustedKeyStore;

    private ReloadableX509TrustManager reloadableX509TrustManager;

    private ReloadableX509KeyManager reloadableX509KeyManager;

    @Inject
    public KeyStoreManagerImpl( final CryptoHelper crypto,
                                final KeyStoreManagerConfiguration config )
    {
        this.crypto = checkNotNull( crypto );
        this.config = checkNotNull( config );

        File dir = config.getBaseDir();
        dir.mkdirs();

        this.privateKeyStore = initializePrivateKeyStore( new File( dir, PRIVATE_KEY_STORE_NAME ) );
        this.trustedKeyStore = initializeTrustedKeyStore( new File( dir, TRUSTED_KEY_STORE_NAME ) );
    }

    /**
     * Initializes the key-store with a default key (if not already created), so that the identity key can be removed
     * w/o invalidating the re-loadable key-manager.
     * <p/>
     * This key is never used by anything.  Its only here to prevent the key-store from being empty of keys.
     */
    private KeystoreInstance initializePrivateKeyStore( final File file )
    {
        log.debug( "Initializing private key-store: {}", file );

        FileKeystoreInstance ks = new FileKeystoreInstance(
            crypto,
            file,
            PRIVATE_KEY_STORE_NAME,
            config.getPrivateKeyStorePassword(),
            config.getKeyStoreType(),
            PRIVATE_KEY_ALIAS + "=" + config.getPrivateKeyStorePassword() );

        if ( !isKeyPairInstalled( ks, DEFAULT00_KEY_ALIAS ) )
        {
            try
            {
                log.debug( "Initializing private key-store" );

                ks.generateKeyPair(
                    DEFAULT00_KEY_ALIAS,
                    config.getPrivateKeyStorePassword().toCharArray(),
                    config.getPrivateKeyPassword().toCharArray(),
                    config.getKeyAlgorithm(),
                    config.getKeyAlgorithmSize(),
                    config.getSignatureAlgorithm(),
                    config.getCertificateValidity().toDaysI(),
                    DEFAULT00_KEY_ALIAS,
                    "Nexus",            //NON-NLS
                    "Sonatype",         //NON-NLS
                    "Silver Spring",    //NON-NLS
                    "MD",               //NON-NLS
                    "US"                //NON-NLS
                );

                Certificate cert =
                    ks.getCertificate( DEFAULT00_KEY_ALIAS, config.getPrivateKeyStorePassword().toCharArray() );
                log.trace( "Generated default certificate:\n{}", cert );
            }
            catch ( KeystoreException e )
            {
                log.error( "Failed to install default certificate", e );
                throw Throwables.propagate( e );
            }
        }

        // List key aliases for sanity
        if ( log.isTraceEnabled() )
        {
            try
            {
                String[] aliases = ks.listPrivateKeys( config.getPrivateKeyStorePassword().toCharArray() );
                if ( aliases != null && aliases.length != 0 )
                {
                    log.trace( "Private key aliases:" );
                    for ( String alias : aliases )
                    {
                        log.trace( "  {}", alias );
                    }
                }
            }
            catch ( KeystoreException e )
            {
                log.error( "Failed to list key aliases", e );
                // this probably won't ever happen
            }
        }

        log.debug( "Private key-store initialized" );

        return ks;
    }

    private KeystoreInstance initializeTrustedKeyStore( final File file )
    {
        log.debug( "Initializing trusted key-store: {}", file );

        FileKeystoreInstance ks =
            new FileKeystoreInstance(
                crypto,
                file,
                TRUSTED_KEY_STORE_NAME,
                config.getTrustedKeyStorePassword(),
                config.getKeyStoreType(),
                TRUSTED_KEY_STORE_NAME + "=" + config.getTrustedKeyStorePassword() );

        logTrustedCertificateAliases( ks );

        // FIXME: Log a warning for edge cases when trust store is not empty, it should be (we manage this content via capability),
        // FIXME: ... probably some bugs related to be fixed
        try
        {
            if ( ks.listTrustCertificates( config.getTrustedKeyStorePassword().toCharArray() ).length != 0 )
            {
                log.warn( "Trusted key-store should have been empty when initialized but was not" );
            }
        }
        catch ( KeystoreException ignore )
        {
        }

        log.debug( "Trusted key-store initialized" );

        return ks;
    }

    private void logTrustedCertificateAliases( final KeystoreInstance ks )
    {
        assert ks != null;

        if ( log.isTraceEnabled() )
        {
            try
            {
                String[] aliases = ks.listTrustCertificates( config.getTrustedKeyStorePassword().toCharArray() );
                if ( aliases != null && aliases.length != 0 )
                {
                    log.trace( "Trusted certificate aliases:" );
                    for ( String alias : aliases )
                    {
                        log.trace( "  {}", alias );
                    }
                }
            }
            catch ( KeystoreException e )
            {
                log.error( "Failed to list aliases", e );
                // this probably won't ever happen
            }
        }
    }

    @Override
    public TrustManager[] getTrustManagers()
        throws KeystoreException
    {
        TrustManager[] trustManagers = trustedKeyStore.getTrustManager( config.getTrustManagerAlgorithm(),
                                                                        config.getTrustedKeyStorePassword().toCharArray() );

        // important! any time we get the array of trust managers we need to replace the X509TrustManager with the
        // ReloadableX509TrustManager so that changes to the keystore are updated in the TrustManager
        try
        {
            reloadableX509TrustManager =
                ReloadableX509TrustManager.replaceX509TrustManager( reloadableX509TrustManager, trustManagers );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new KeystoreException( "A ReloadableX509TrustManager could not be created.", e );
        }
        return trustManagers;
    }

    @Override
    public KeyManager[] getKeyManagers()
        throws KeystoreException
    {
        KeyManager[] keyManagers = privateKeyStore.getKeyManager( config.getKeyManagerAlgorithm(), PRIVATE_KEY_ALIAS,
                                                                  config.getPrivateKeyStorePassword().toCharArray() );

        // important! any time we get the array of key managers we need to replace the X509KeyManager with the
        // ReloadableX509KeyManager so that changes to the keystore are updated in the KeyManager
        try
        {
            reloadableX509KeyManager =
                ReloadableX509KeyManager.replaceX509KeyManager( reloadableX509KeyManager, keyManagers );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new KeystoreException( "A ReloadableX509KeyManager could not be created.", e );
        }
        return keyManagers;
    }

    @Override
    public void importTrustCertificate( Certificate certificate, String alias )
        throws KeystoreException
    {
        log.debug( "Importing trust certificate w/alias: {}", alias );

        if ( trustedKeyStore.getCertificate( alias ) != null )
        {
            log.warn( "Certificate already exists in trust-store w/alias: {}; replacing certificate", alias );
            trustedKeyStore.deleteEntry( alias, config.getTrustedKeyStorePassword().toCharArray() );
        }

        trustedKeyStore.importTrustCertificate( certificate, alias, config.getTrustedKeyStorePassword().toCharArray() );

        logTrustedCertificateAliases( trustedKeyStore );

        // update re-loadable bits
        getTrustManagers();
    }

    @Override
    public void importTrustCertificate( String certificateInPEM, String alias )
        throws KeystoreException,
        CertificateParsingException
    {
        // parse the cert
        Certificate certificate = CertificateUtil.decodePEMFormattedCertificate( certificateInPEM );
        // then import it
        importTrustCertificate( certificate, alias );
    }

    @Override
    public Certificate getTrustedCertificate( String alias )
        throws KeystoreException
    {
        return trustedKeyStore.getCertificate(
            checkNotNull( alias, "'alias' cannot be null when looking up a trusted Certificate." ), //NON-NLS
            config.getTrustedKeyStorePassword().toCharArray() );
    }

    @Override
    public Collection<Certificate> getTrustedCertificates()
        throws KeystoreException
    {
        String[] aliases = trustedKeyStore.listTrustCertificates( config.getTrustedKeyStorePassword().toCharArray() );
        List<Certificate> certificates = Lists.newArrayListWithCapacity( aliases.length );
        for ( String alias : aliases )
        {
            Certificate cert = trustedKeyStore.getCertificate( alias );
            // FIXME: Work around some strange case not clear why, but alias is reported for non-existent/removed certs
            if ( cert == null )
            {
                log.warn( "Trust-store reports it contains certificate for alias '{}' but certificate is null" );
                continue;
            }
            certificates.add( cert );
        }

        return certificates;
    }

    @Override
    public void removeTrustCertificate( String alias )
        throws KeystoreException
    {
        log.debug( "Removing trust certificate w/alias: {}", alias );

        trustedKeyStore.deleteEntry( alias, config.getTrustedKeyStorePassword().toCharArray() );

        logTrustedCertificateAliases( trustedKeyStore );

        // update re-loadable bits
        getTrustManagers();
    }

    @Override
    public void generateAndStoreKeyPair( String commonName, String organizationalUnit, String organization,
                                         String locality, String state, String country )
        throws KeystoreException
    {
        privateKeyStore.generateKeyPair( PRIVATE_KEY_ALIAS,
                                         config.getPrivateKeyStorePassword().toCharArray(),
                                         config.getPrivateKeyPassword().toCharArray(),
                                         config.getKeyAlgorithm(),
                                         config.getKeyAlgorithmSize(),
                                         config.getSignatureAlgorithm(),
                                         config.getCertificateValidity().toDaysI(),
                                         commonName,
                                         organizationalUnit,
                                         organization,
                                         locality,
                                         state,
                                         country );

        // update re-loadable bits
        getKeyManagers();
    }

    private boolean isKeyPairInstalled( final KeystoreInstance ks, final String alias )
    {
        try
        {
            ks.getCertificate( alias, config.getPrivateKeyStorePassword().toCharArray() );
            return true;
        }
        catch ( KeystoreException e )
        {
            log.trace( "Key-pair not installed w/alias: {}", alias );
            return false;
        }
    }

    @Override
    public boolean isKeyPairInitialized()
    {
        return isKeyPairInstalled( privateKeyStore, PRIVATE_KEY_ALIAS );
    }

    @Override
    public Certificate getCertificate()
        throws KeystoreException
    {
        return privateKeyStore.getCertificate( PRIVATE_KEY_ALIAS, config.getPrivateKeyStorePassword().toCharArray() );
    }

    @Override
    public PrivateKey getPrivateKey()
        throws KeystoreException
    {
        return privateKeyStore.getPrivateKey( PRIVATE_KEY_ALIAS, config.getPrivateKeyStorePassword().toCharArray(),
                                              config.getPrivateKeyPassword().toCharArray() );
    }

    @Override
    public void removePrivateKey()
        throws KeystoreException
    {
        privateKeyStore.deleteEntry( PRIVATE_KEY_ALIAS, config.getPrivateKeyStorePassword().toCharArray() );
    }

}
