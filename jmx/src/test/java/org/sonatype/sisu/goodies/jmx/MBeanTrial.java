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

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * MBean trials.
 */
public class MBeanTrial
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

    private int getProcessId() {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');
        if (index < 1) {
            throw new RuntimeException();
        }
        return Integer.parseInt(jvmName.substring(0, index));
    }

    private void launchVisualVm() throws IOException {
        // NOTE: This only appears to work when set on the JVM directly as arguments
        //String displayName = getClass().getSimpleName() + "_" + System.currentTimeMillis();
        //log("Display name: {}", displayName);
        //System.setProperty("visualvm.display.name", displayName);

        int pid = getProcessId();
        log("JVM PID: {}", pid);

        final Process proc = Runtime.getRuntime().exec("jvisualvm --openpid " + pid);
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                proc.destroy();
            }
        });
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

        launchVisualVm();

        synchronized (this) {
            wait();
        }
    }
}
