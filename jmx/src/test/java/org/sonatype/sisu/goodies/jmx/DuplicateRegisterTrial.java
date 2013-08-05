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

package org.sonatype.sisu.goodies.jmx;

import javax.management.InstanceAlreadyExistsException;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

/**
 * Tests what happens on duplicate registration of mbeans.
 */
public class DuplicateRegisterTrial
    extends TestSupport
{
  private static interface TestObjectMBean
  {
    // ignore
  }

  private static class TestObject
      extends StandardMBeanSupport
      implements TestObjectMBean
  {
    private TestObject() {
      super(TestObjectMBean.class, false);
    }
  }

  @Test(expected = InstanceAlreadyExistsException.class)
  public void registerDuplicate() throws Exception {
    ObjectNameBuilder builder = new ObjectNameBuilder().domain("test");

    MBeans.register(builder.copy()
        .type("test")
        .build(),
        new TestObject());

    MBeans.register(builder.copy()
        .type("test")
        .build(),
        new TestObject());
  }
}
