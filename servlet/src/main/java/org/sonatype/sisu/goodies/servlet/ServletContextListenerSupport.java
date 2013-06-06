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

package org.sonatype.sisu.goodies.servlet;

import com.google.common.base.Throwables;
import org.sonatype.sisu.goodies.common.ComponentSupport;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Support for {@link ServletContextListener} implementations.
 *
 * @since 1.7
 */
public abstract class ServletContextListenerSupport
    extends ComponentSupport
    implements ServletContextListener
{
    public void contextInitialized(final ServletContextEvent event) {
        try {
            log.info("Initializing");
            initialize(event.getServletContext());
            log.info("Initialized");
        }
        catch (Exception e) {
            log.error("Failed to initialize", e);
            throw Throwables.propagate(e);
        }
    }

    protected abstract void initialize(final ServletContext context) throws Exception;

    public void contextDestroyed(final ServletContextEvent event) {
        try {
            log.info("Destroying");
            destroy(event.getServletContext());
            log.info("Destroyed");
        }
        catch (Exception e) {
            log.error("Failed to destroy", e);
        }
    }

    protected abstract void destroy(final ServletContext context) throws Exception;
}
