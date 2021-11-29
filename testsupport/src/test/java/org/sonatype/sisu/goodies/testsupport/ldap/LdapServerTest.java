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
package org.sonatype.sisu.goodies.testsupport.ldap;

import java.io.File;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import org.sonatype.sisu.goodies.testsupport.ldap.LdapServer;

import org.apache.directory.api.ldap.model.constants.SupportedSaslMechanisms;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * This may sound silly, but this test actually tests test LDAP server.
 */
public class LdapServerTest
{
  private static final String SYSPROP_SSLTRUSTSTORE = "javax.net.ssl.trustStore";
  private static final String AUTH_CRAMMD5 = "CRAM-MD5";
  private static final String AUTH_DIGESTMD5 = "DIGEST-MD5";
  private static final String AUTH_SIMPLE = "simple";
  private static final String AUTH_NONE = "none";

  private LdapServer server = new LdapServer();

  @After
  public void stopServer() throws Exception {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  @Test
  public void testAnonymous() throws Exception {
    server.start();

    assertLogin(AUTH_NONE, AUTH_SIMPLE);
    assertLoginFailure(AUTH_DIGESTMD5, AUTH_CRAMMD5);
  }

  @Test
  public void testSimple() throws Exception {
    server.setAuthenticationSimple();
    server.start();

    assertLogin(AUTH_SIMPLE);
    assertLoginFailure(AUTH_NONE, AUTH_DIGESTMD5, AUTH_CRAMMD5);
  }

  @Test
  public void testDigest() throws Exception {
    server.setAuthenticationSasl(SupportedSaslMechanisms.DIGEST_MD5);
    server.start();

    assertLogin(AUTH_DIGESTMD5);
    assertLoginFailure(AUTH_NONE, AUTH_SIMPLE, AUTH_CRAMMD5);
  }

  @Test
  public void testCram() throws Exception {
    server.setAuthenticationSasl(SupportedSaslMechanisms.CRAM_MD5);
    server.start();

    assertLogin(AUTH_CRAMMD5);
    assertLoginFailure(AUTH_NONE, AUTH_SIMPLE, AUTH_DIGESTMD5);
  }

  @Test
  public void testInvalidSaslRealm() throws Exception {
    server.setAuthenticationSasl(SupportedSaslMechanisms.DIGEST_MD5);
    server.start();

    Hashtable<String, Object> env = getEnv(AUTH_DIGESTMD5);
    env.put("java.naming.security.sasl.realm", "wrongrealm");
    try {
      new InitialDirContext(env).close();
      Assert.fail();
    }
    catch (NamingException expected) {
      assertThat(expected.toString(), containsString("Nonexistent realm: wrongrealm"));
    }
  }

  @Test
  public void testNoSaslRealm() throws Exception {
    server.setAuthenticationSasl(SupportedSaslMechanisms.DIGEST_MD5);
    server.start();

    Hashtable<String, Object> env = getEnv(AUTH_DIGESTMD5);
    env.remove("java.naming.security.sasl.realm");

    // this is apparently client-only affair, so this is expected to work

    new InitialDirContext(env).close();
  }

  @Test
  public void testLdaps() throws Exception {
    server.enableLdaps(new File("src/test/resources/keystore/test.ks"), "secret");
    server.start();

    String origTruststore = System.getProperty(SYSPROP_SSLTRUSTSTORE);
    try {
      System.setProperty(SYSPROP_SSLTRUSTSTORE,
          new File("src/test/resources/keystore/testclient.ks").getCanonicalPath());
      assertLogin(AUTH_NONE);
    }
    finally {
      if (origTruststore != null) {
        System.setProperty(SYSPROP_SSLTRUSTSTORE, origTruststore);
      }
      else {
        System.getProperties().remove(SYSPROP_SSLTRUSTSTORE);
      }
    }
  }

  private void assertLogin(String... mechanisms) throws NamingException {
    for (String mechanism : mechanisms) {
      login(mechanism);
    }
  }

  private void assertLoginFailure(String... mechanisms) throws NamingException {
    for (String mechanism : mechanisms) {
      try {
        login(mechanism);
        Assert.fail();
      }
      catch (AuthenticationException expected) {
        // oddly, apacheds throws auth exception for unsupported simple auth
      }
      catch (AuthenticationNotSupportedException expected) {
      }
    }
  }

  private void login(String mechanism) throws NamingException {
    new InitialDirContext(getEnv(mechanism)).close();
  }

  private Hashtable<String, Object> getEnv(String mechanism) {
    Hashtable<String, Object> env = new Hashtable<String, Object>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, server.getUrl());
    env.put(Context.SECURITY_AUTHENTICATION, mechanism);
    if (AUTH_SIMPLE.equals(mechanism)) {
      env.put(Context.SECURITY_PRINCIPAL, server.getSystemUserDN());
      env.put(Context.SECURITY_CREDENTIALS, server.getSystemUserPassword());
    }
    else if (AUTH_CRAMMD5.equals(mechanism) || AUTH_DIGESTMD5.equals(mechanism)) {
      env.put(Context.SECURITY_PRINCIPAL, server.getSystemUser());
      env.put(Context.SECURITY_CREDENTIALS, server.getSystemUserPassword());
      env.put("java.naming.security.sasl.realm", server.getSaslRealm());
    }
    return env;
  }
}
