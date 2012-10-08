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
import org.softee.management.annotation.Description;
import org.softee.management.annotation.MBean;
import org.softee.management.annotation.ManagedAttribute;
import org.softee.management.helper.MBeanRegistration;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import javax.management.MXBean;
import java.util.Date;

/**
 * Trials of Pojo-MBean library with MXBeans.
 */
public class PojoMXBeanTrial
    extends TestSupport
{
    // NOTE: This does not work, the bean is not an MXBean and the NamedInteger will not render as opentype data

    @MBean(objectName = "test:type=TestApplication")
    public static class TestApplication
    {
        @ManagedAttribute
        @Description("Current date")
        public NamedInteger getDate() {
            Date date = new Date();
            return new NamedInteger(date.toString(), date.getTime());
        }
    }

    @Test
    public void runApplicationTest() throws Exception {
        TestApplication app = new TestApplication();
        new MBeanRegistration(app).register();
        VisualVmHelper.openCurrentPid().waitFor();
    }
}
