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
import org.softee.management.annotation.ManagedOperation;
import org.softee.management.annotation.Parameter;
import org.softee.management.helper.MBeanRegistration;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Trials of Pojo-MBean library with MBeans.
 */
public class PojoMBeanTrial
    extends TestSupport
{
    @MBean(objectName = "test:type=CountingApplication")
    @Description("This Java application shows how to expose a Read/Write int property and two methods as an MBean")
    public class CountingApplication
        extends Thread
    {
        private final AtomicInteger counter = new AtomicInteger();

        public CountingApplication() {
            setDaemon(true);
        }

        public void run() {
            try {
                MBeanRegistration registration = new MBeanRegistration(this);
                registration.register();

                while (true) {
                    incrementCounter(1);
                    Thread.sleep(1000);
                }
            }
            catch (Exception e) {
                log(e);
            }
        }

        @ManagedAttribute
        @Description("A counter variable")
        public int getCounter() {
            return counter.get();
        }

        // NOTE: Immutable, so we can get a graph to display
        //@ManagedAttribute
        //public void setCounter(int counter) {
        //    this.counter.set(counter);
        //}

        @ManagedOperation
        @Description("Increments the counter by the requested amount and shows the resulting value")
        public int incrementCounter(@Parameter("amount") @Description("The amount to increment the counter with") int delta) {
            return counter.addAndGet(delta);
        }

        @ManagedOperation
        @Description("Resets the counter")
        public void reset() {
            counter.set(0);
        }
    }

    @Test
    public void runApplicationTest() throws Exception {
        new CountingApplication().start();
        VisualVmHelper.openCurrentPid().waitFor();
    }
}
