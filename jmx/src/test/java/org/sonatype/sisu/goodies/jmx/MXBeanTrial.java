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
package org.sonatype.sisu.goodies.jmx;

import java.util.Date;

import org.sonatype.sisu.goodies.testsupport.TestSupport;

import org.junit.Test;

/**
 * MXBean trials.
 */
public class MXBeanTrial
    extends TestSupport
{
  public static interface TestObjectMXBean
  {
    NamedInteger getData();
  }

  public static class TestObject
      extends StandardMBeanSupport
      implements TestObjectMXBean
  {
    private TestObject() {
      super(TestObjectMXBean.class, true);
    }

    @Override
    public NamedInteger getData() {
      Date date = new Date();
      return new NamedInteger(date.toString(), date.getTime());
    }
  }

  // NOTE: For this to render as desired, the mbean inspector needs to order keys when rendering the tree:
  // NOTE: type,Server,Application,Module,name

  @Test
  public void buildSimpleTree() throws Exception {
    ObjectNameBuilder builder = new ObjectNameBuilder().domain("test");

    MBeans.register(builder.copy()
        .type("Server")
        .name("server1")
        .build(),
        new TestObject());

    MBeans.register(builder.copy()
        .property("Server", "server1")
        .type("Server.Application")
        .name("app1")
        .build(),
        new TestObject());

    MBeans.register(builder.copy()
        .property("Server", "server1")
        .property("Application", "app1")
        .type("Server.Application.Module")
        .name("module1")
        .build(),
        new TestObject());

    MBeans.register(builder.copy()
        .property("Server", "server1")
        .property("Application", "app1")
        .property("Module", "module1")
        .type("Server.Application.Module.Component")
        .name("component1")
        .build(),
        new TestObject());

    MBeans.register(builder.copy()
        .property("Server", "server1")
        .property("Application", "app1")
        .property("Module", "module1")
        .type("Server.Application.Module.Component")
        .name("component2")
        .build(),
        new TestObject());

    VisualVmHelper.openCurrentPid().waitFor();
  }
}
