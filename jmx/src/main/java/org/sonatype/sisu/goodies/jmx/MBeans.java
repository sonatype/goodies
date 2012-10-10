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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * MBean helpers.
 *
 * @since 1.5
 */
public class MBeans
{
    private static final Logger log = LoggerFactory.getLogger(MBeans.class);

    public static MBeanServer getServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public static void register(final ObjectName objectName, final Object object) {
        checkNotNull(objectName);
        checkNotNull(object);

        log.debug("Register mbean: {}", objectName);

        try {
            MBeanServer server = getServer();

            if (server.isRegistered(objectName)) {
                log.warn("MBean already registerd with name: {}", objectName);
            }

            server.registerMBean(object, objectName);
        }
        catch (Exception e) {
            log.warn("Failed to register mbean: {}", objectName, e);
        }
    }

    public static void unregister(final ObjectName objectName) {
        checkNotNull(objectName);

        log.debug("Unregister mbean: {}", objectName);

        try {
            MBeanServer server = getServer();
            if (server.isRegistered(objectName)) {
                server.unregisterMBean(objectName);
            }
            else {
                log.debug("Ignoring; mbean is not registered: {}", objectName);
            }
        }
        catch (Exception e) {
            log.warn("Failed to unregister mbean: {}", objectName, e);
        }
    }
}
