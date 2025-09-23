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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Embedded LDAP server meant to facilitate unit testing of LDAP integration. The LDAP server instance must be started
 * explicitly, but it is stopped automatically by junit.
 *
 * @since 1.13
 */
public class LdapServerRule
    extends LdapServer
    implements TestRule
{
  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement()
    {
      @Override
      public void evaluate() throws Throwable {
        try {
          base.evaluate();
        }
        finally {
          stop();
        }
      }
    };
  }
}
