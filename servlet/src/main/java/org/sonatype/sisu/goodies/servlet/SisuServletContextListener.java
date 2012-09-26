/*
 * Copyright (c) 2007-2012 Sonatype, Inc.  All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/central-secure/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.sisu.goodies.servlet;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.guice.bean.binders.WireModule;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Sisu {@link GuiceServletContextListener}.
 *
 * @since 1.5
 */
public class SisuServletContextListener
    extends GuiceServletContextListener
{
    @NonNls
    public static final String INJECTOR_KEY = "@INJECTOR";

    @NonNls
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ServletContext servletContext;

    private Injector injector;

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        checkNotNull(event);

        // capture the servlet context, some modules may need this and otherwise have no access to it (like shiro modules)
        servletContext = event.getServletContext();

        // We need to set the injector here first because super.contextInitialized() will call getInjector() so if we have not retrieved
        // our injector created elsewhere, say from a testing environment, a new one will be created and cause inconsistencies.
        injector = (Injector) event.getServletContext().getAttribute(INJECTOR_KEY);

        super.contextInitialized(event);
    }

    protected ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    protected Injector getInjector() {
        if (injector == null) {
            injector = createInjector();
        }
        return injector;
    }

    protected Injector createInjector() {
        List<Module> modules = Lists.newArrayList();

        configureModules(modules);

        if (log.isDebugEnabled() && !modules.isEmpty()) {
            log.debug("Modules:");
            for (Module module : modules) {
                log.debug("  {}", module);
            }
        }

        return Guice.createInjector(new WireModule(modules));
    }

    protected void configureModules(final List<Module> modules) {
        // empty
    }
}
