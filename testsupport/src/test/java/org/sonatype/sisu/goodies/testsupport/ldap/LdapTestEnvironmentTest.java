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
import javax.naming.InitialContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.apache.directory.server.constants.ServerDNConstants;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LdapTestEnvironmentTest
    extends AbstractLdapTestEnvironment
{
  @Test
  public void smoke() throws Exception {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, "ldap://localhost:" + getLdapServer().getPort() + "/o=sonatype");
    env.put(Context.SECURITY_PRINCIPAL, ServerDNConstants.ADMIN_SYSTEM_DN);
    env.put(Context.SECURITY_CREDENTIALS, "secret");
    env.put(Context.SECURITY_AUTHENTICATION, "simple");

    // Let's open a connection on this partition
    InitialContext initialContext = new InitialLdapContext(env, null);

    // We should be able to read it
    DirContext appRoot = (DirContext) initialContext.lookup("");
    assertThat(appRoot, notNullValue());

    // Let's get the entry associated to the top level
    Attributes attributes = appRoot.getAttributes("");
    assertThat(attributes, notNullValue());
    assertThat((String) attributes.get("o").get(), equalTo("sonatype"));

    Attribute attribute = attributes.get("objectClass");
    assertThat(attribute, notNullValue());
    assertThat(attribute.contains("top"), is(true));
    assertThat(attribute.contains("organization"), is(true));
  }
}
