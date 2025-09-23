/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.testsupport.ldap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sonatype.goodies.testsupport.port.PortRegistry;

import com.google.common.annotations.VisibleForTesting;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import org.apache.commons.io.FileUtils;
import org.apache.directory.api.ldap.model.constants.AuthenticationLevel;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.constants.SupportedSaslMechanisms;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.InterceptorEnum;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.authn.AnonymousAuthenticator;
import org.apache.directory.server.core.authn.AuthenticationInterceptor;
import org.apache.directory.server.core.authn.Authenticator;
import org.apache.directory.server.core.authn.SimpleAuthenticator;
import org.apache.directory.server.core.authn.StrongAuthenticator;
import org.apache.directory.server.core.factory.AvlPartitionFactory;
import org.apache.directory.server.core.factory.PartitionFactory;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.ldap.handlers.sasl.MechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.cramMD5.CramMd5MechanismHandler;
import org.apache.directory.server.ldap.handlers.sasl.digestMD5.DigestMd5MechanismHandler;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.protocol.shared.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedded LDAP server meant to facilitate unit testing of LDAP integration. For convenience, use the
 * LdapServerRule subclass in tests.
 *
 * @since 1.13
 */
public class LdapServer
{
  private static final Logger log = LoggerFactory.getLogger(LdapServer.class);

  private static final String LOCALHOST = "localhost";

  private File workingDirectory;

  private PortRegistry portRegistry;

  private DefaultDirectoryService directoryService;

  private org.apache.directory.server.ldap.LdapServer ldapServer;

  private int port;

  private AuthenticationLevel authLevel = AuthenticationLevel.NONE;

  private Map<String, MechanismHandler> saslHandlers = new HashMap<String, MechanismHandler>();

  private String searchBaseDn = "ou=system";

  private File ldapsKeystore;

  private String ldapsKeystorePassword;

  private boolean running = false;

  public LdapServer(File workingDirectory) {
    this(workingDirectory, new PortRegistry());
  }

  public LdapServer(File workingDirectory, PortRegistry portRegistry) {
    log.debug("Creating LdapServer with workingDirectory={}",
        workingDirectory == null ? null : workingDirectory.getAbsolutePath());
    this.workingDirectory = workingDirectory;
    this.portRegistry = portRegistry;
  }

  /**
   * Creates new LdapServer instance with conventional work directory target/apacheds
   */
  public LdapServer() {
    this(initWorkingDirectory());
  }

  private static File initWorkingDirectory() {
    File workingDirectory = new File("target/apacheds");
    try {
      FileUtils.deleteDirectory(workingDirectory);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return workingDirectory;
  }

  public void start() throws Exception {
    if (running) {
      throw new IllegalStateException("The LdapServer is already running");
    }

    long start = System.currentTimeMillis();

    if (port <= 0) {
      port = portRegistry.reservePort();
    }

    // an example that shows how to create and configure embedded apacheds instance
    // http://svn.apache.org/repos/asf/directory/apacheds/trunk/core-annotations/src/main/java/org/apache/directory/server/core/factory/DefaultDirectoryServiceFactory.java

    directoryService = new DefaultDirectoryService();

    // support multiple embedded ldap servers by assigning each one a distinct cache
    URL configURL = getClass().getClassLoader().getResource("directory-cacheservice.xml");
    Configuration config = ConfigurationFactory.parseConfiguration(configURL);
    config.setName(config.getName() + '_' + System.identityHashCode(this));
    directoryService.setCacheService(new CacheService(new CacheManager(config)));

    directoryService.setInstanceLayout(new InstanceLayout(workingDirectory));

    SchemaManager schemaManager = new DefaultSchemaManager();
    directoryService.setSchemaManager(schemaManager);

    schemaManager.enable("nis"); // required by group mapping tests

    initPartitions(directoryService);

    ldapServer = new org.apache.directory.server.ldap.LdapServer();

    Transport transport = new TcpTransport(LOCALHOST, port);
    transport.setEnableSSL(ldapsKeystore != null);
    ldapServer.setTransports(transport);
    if (ldapsKeystore != null) {
      ldapServer.setKeystoreFile(ldapsKeystore.getCanonicalPath());
    }
    if (ldapsKeystorePassword != null) {
      ldapServer.setCertificatePassword(ldapsKeystorePassword);
    }

    ldapServer.setDirectoryService(directoryService);

    // allowed authentication mechanisms
    Authenticator[] authenticators;
    switch (authLevel) {
      case SIMPLE:
        authenticators = new Authenticator[] { new SimpleAuthenticator() };
        break;
      case STRONG:
        authenticators = new Authenticator[] { new StrongAuthenticator() };
        ldapServer.setSaslMechanismHandlers(saslHandlers);
        ldapServer.setSaslHost(LOCALHOST);
        ldapServer.setSaslRealms(Arrays.asList(getSaslRealm()));
        ldapServer.setSearchBaseDn(searchBaseDn);
        break;
      case NONE:
      default:
        directoryService.setAllowAnonymousAccess(true);
        authenticators = new Authenticator[] { new AnonymousAuthenticator(), new SimpleAuthenticator() };
        break;
    }
    AuthenticationInterceptor auth = (AuthenticationInterceptor) directoryService
        .getInterceptor(InterceptorEnum.AUTHENTICATION_INTERCEPTOR.getName());
    auth.setAuthenticators(authenticators);

    directoryService.startup();
    ldapServer.start();

    running = true;

    log.debug("Started LdapServer in {} ms", System.currentTimeMillis() - start);
  }

  public void loadData(String ldifResourceName) throws IOException {
    File ldif = new File(workingDirectory, "data" + ldifResourceName);
    FileUtils.copyURLToFile(LdapServer.class.getResource(ldifResourceName), ldif);
    loadData(ldif);
  }

  public void loadData(File ldif) {
    new LdifFileLoader(directoryService.getAdminSession(), ldif.getAbsolutePath()).execute();
  }

  private static void initPartitions(DefaultDirectoryService directoryService) throws Exception {
    LdifPartition ldifPartition = new LdifPartition(directoryService.getSchemaManager(),
        directoryService.getDnFactory());
    ldifPartition.setPartitionPath(new File(directoryService.getInstanceLayout().getPartitionsDirectory(), "schema")
        .toURI());
    SchemaPartition schemaPartition = new SchemaPartition(directoryService.getSchemaManager());
    schemaPartition.setWrappedPartition(ldifPartition);
    directoryService.setSchemaPartition(schemaPartition);
    PartitionFactory partitionFactory = new AvlPartitionFactory();

    Partition systemPartition = partitionFactory.createPartition(directoryService.getSchemaManager(), directoryService
        .getDnFactory(), "system", ServerDNConstants.SYSTEM_DN, 500, new File(directoryService.getInstanceLayout()
        .getPartitionsDirectory(), "system"));
    systemPartition.setSchemaManager(directoryService.getSchemaManager());
    partitionFactory.addIndex(systemPartition, SchemaConstants.OBJECT_CLASS_AT, 100);
    directoryService.setSystemPartition(systemPartition);

    Partition sonatypePartition = partitionFactory.createPartition(directoryService.getSchemaManager(),
        directoryService.getDnFactory(), "sonatype", "o=sonatype", 500, new File(directoryService.getInstanceLayout()
            .getPartitionsDirectory(), "sonatype"));
    sonatypePartition.setSchemaManager(directoryService.getSchemaManager());
    partitionFactory.addIndex(sonatypePartition, SchemaConstants.OBJECT_CLASS_AT, 100);
    directoryService.addPartition(sonatypePartition);

    Partition groupsPartition = partitionFactory.createPartition(directoryService.getSchemaManager(), directoryService
        .getDnFactory(), "groups", "ou=groups,dc=company,dc=com", 500, new File(directoryService.getInstanceLayout()
        .getPartitionsDirectory(), "groups"));
    groupsPartition.setSchemaManager(directoryService.getSchemaManager());
    partitionFactory.addIndex(groupsPartition, SchemaConstants.OBJECT_CLASS_AT, 100);
    directoryService.addPartition(groupsPartition);

    Partition usersPartition = partitionFactory.createPartition(directoryService.getSchemaManager(), directoryService
        .getDnFactory(), "users", "ou=users,dc=company,dc=com", 500, new File(directoryService.getInstanceLayout()
        .getPartitionsDirectory(), "users"));
    usersPartition.setSchemaManager(directoryService.getSchemaManager());
    partitionFactory.addIndex(usersPartition, SchemaConstants.OBJECT_CLASS_AT, 100);
    directoryService.addPartition(usersPartition);

    Partition acmeBrickPartition = partitionFactory.createPartition(directoryService.getSchemaManager(),
        directoryService.getDnFactory(), "acme_brick", "dc=acme brick,dc=com", 500, new File(directoryService
            .getInstanceLayout().getPartitionsDirectory(), "acme_brick"));
    acmeBrickPartition.setSchemaManager(directoryService.getSchemaManager());
    partitionFactory.addIndex(acmeBrickPartition, SchemaConstants.OBJECT_CLASS_AT, 100);
    directoryService.addPartition(acmeBrickPartition);
  }

  public void suspend() throws Exception {
    ldapServer.stop();
  }

  public void resume() throws Exception {
    ldapServer.start();
  }

  public void stop() throws Exception {
    if (!running) {
      return;
    }

    long start = System.currentTimeMillis();

    ldapServer.stop();
    directoryService.shutdown();
    portRegistry.releasePort(port);
    port = 0;
    running = false;

    log.debug("Stopped LdapServer in {} ms", System.currentTimeMillis() - start);
  }

  public String getUrl() {
    StringBuilder sb = new StringBuilder();
    sb.append(ldapsKeystore != null ? "ldaps" : "ldap");
    sb.append("://" + LOCALHOST + ":");
    sb.append(port);
    return sb.toString();
  }

  public String getHostname() {
    return LOCALHOST;
  }

  public int getPort() {
    return port;
  }

  public String getSystemUserDN() {
    return "uid=admin,ou=system";
  }

  public String getSystemUser() {
    return "admin";
  }

  public String getSystemUserPassword() {
    return "secret";
  }

  public void setAuthenticationSimple() {
    authLevel = AuthenticationLevel.SIMPLE;
  }

  public void setAuthenticationSasl(String mechanism) {
    authLevel = AuthenticationLevel.STRONG;
    if (SupportedSaslMechanisms.DIGEST_MD5.equals(mechanism)) {
      saslHandlers = Collections.singletonMap(mechanism, (MechanismHandler) new DigestMd5MechanismHandler());
    }
    else if (SupportedSaslMechanisms.CRAM_MD5.equals(mechanism)) {
      saslHandlers = Collections.singletonMap(mechanism, (MechanismHandler) new CramMd5MechanismHandler());
    }
  }

  public void setSearchBaseDn(String searchBaseDn) {
    this.searchBaseDn = searchBaseDn;
  }

  public String getSaslRealm() {
    return LOCALHOST;
  }

  public void enableLdaps(File keystore, String keystorePassword) {
    this.ldapsKeystore = keystore;
    this.ldapsKeystorePassword = keystorePassword;
  }

  @VisibleForTesting
  SchemaManager getSchemaManager() {
    return directoryService.getSchemaManager();
  }
}
