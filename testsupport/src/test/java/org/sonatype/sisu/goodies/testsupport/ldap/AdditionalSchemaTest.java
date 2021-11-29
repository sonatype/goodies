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

import java.util.Map;

import org.apache.directory.api.ldap.model.schema.registries.Schema;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

public class AdditionalSchemaTest
    extends AbstractLdapTestEnvironment
{
  @Test
  public void additionalSchemas() {
    Map<String, Schema> schemas = getLdapServer().getSchemaManager().getRegistries().getLoadedSchemas();
    assertThat(schemas, hasKey("nis"));
    assertThat(schemas.get("nis").isDisabled(), is(false));
  }
}
