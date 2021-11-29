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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.sonatype.sisu.goodies.testsupport.ldap.LdapServer;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SaslLdapTestEnvironmentTest
    extends AbstractLdapTestEnvironment
{
  @Override
  protected void setup(LdapServer ldapServer) {
    ldapServer.setSearchBaseDn("ou=people,o=sonatype");
    ldapServer.setAuthenticationSasl("DIGEST-MD5");
  }

  /**
   * Tests to make sure DIGEST-MD5 binds below the RootDSE work.
   */
  @Test
  public void saslDigestMd5Bind() throws Exception {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, "ldap://localhost:" + getLdapServer().getPort());
    env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
    // env.put( Context.SECURITY_PRINCIPAL, "admin" );
    env.put(Context.SECURITY_PRINCIPAL, "tstevens");
    env.put(Context.SECURITY_CREDENTIALS, "tstevens123");

    // Specify realm
    env.put("java.naming.security.sasl.realm", "localhost");
    // Request privacy protection
    env.put("javax.security.sasl.qop", "auth-conf");

    final DirContext context = new InitialDirContext(env);
    String[] attrIDs = { "uid" };
    Attributes attrs = context.getAttributes("uid=tstevens,ou=people,o=sonatype", attrIDs);
    String uid = null;
    if (attrs.get("uid") != null) {
      uid = (String) attrs.get("uid").get();
    }
    assertThat(uid, equalTo("tstevens"));
  }
}
