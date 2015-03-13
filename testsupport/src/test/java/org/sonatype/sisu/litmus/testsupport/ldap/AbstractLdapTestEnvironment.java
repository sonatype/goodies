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
package org.sonatype.sisu.litmus.testsupport.ldap;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLdapTestEnvironment
    extends TestSupport
{
  protected Logger log = LoggerFactory.getLogger(getClass());

  private LdapServer ldapServer;

  public LdapServer getLdapServer() {
    return ldapServer;
  }

  @Before
  public void startLdap() throws Exception {
    ldapServer = new LdapServer(util.createTempDir());
    setup(ldapServer);
    ldapServer.start();
    ldapServer.loadData(util.resolveFile("src/test/resources/sonatype.ldif"));
  }

  protected void setup(LdapServer ldapServer) {
    // customize to override LDAP defaults
  }

  @After
  public void stopLdap() throws Exception {
    ldapServer.stop();
    ldapServer = null;
  }
}
