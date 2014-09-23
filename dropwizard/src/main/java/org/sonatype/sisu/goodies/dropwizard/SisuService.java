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
package org.sonatype.sisu.goodies.dropwizard;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local copy of SisuService from https://github.com/tesla/dropwizard-sisu with various tweaks.
 *
 * @since 1.11
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class SisuService<T extends Configuration>
    extends Application<T>
{
  private static final Logger logger = LoggerFactory.getLogger(SisuService.class);

  private final List<Module> initModules = new ArrayList<Module>();

  private Injector injector = null;

  public Injector getInjector() {
    return injector;
  }

  public <C> C getInstance(Class<C> type) {
    return getInjector().getInstance(type);
  }

  @Override
  public void run(T configuration, Environment environment) throws Exception {
    injector = createInjector(configuration);
    injector.injectMembers(this);
    runWithInjector(configuration, environment, injector);
  }

  private Injector createInjector(final T configuration) {
    List<Module> modules = new ArrayList<Module>();

    modules.add(new AbstractModule()
    {
      @Override
      protected void configure() {
        bind((Class) configuration.getClass()).toInstance(configuration);
      }
    });

    modules.addAll(initModules);

    modules.addAll(modules(configuration));

    ClassSpace space = new URLClassSpace(getClass().getClassLoader());
    modules.add(new SpaceModule(space, scanning(configuration)));

    return Guice.createInjector(new WireModule(modules));
  }

  //
  // Allow the application to customize the scanning
  //
  protected BeanScanning scanning(T configuration) {
    return BeanScanning.ON;
  }

  //
  // Allow the application to customize the modules
  //
  protected List<Module> modules(T configuration) {
    return Collections.emptyList();
  }

  //
  // Allow the application to customize the environment
  //
  protected void customize(T configuration, Environment environment) {
  }

  private void runWithInjector(T configuration, Environment environment, Injector injector) {
    customize(configuration, environment);
    BeanLocator locator = injector.getInstance(BeanLocator.class);
    environment.jersey().register(new SisuComponentProviderFactory(locator));
    addHealthChecks(environment, locator);
    addProviders(environment, locator);
    addInjectableProviders(environment, locator);
    addResources(environment, locator);
    addResourceFilterFactories(environment, locator);
    addTasks(environment, locator);
    addManaged(environment, locator);
  }

  // Allow modules to be added manually
  public void addModule(Module module) {
    this.initModules.add(module);
  }

  public void addModules(Collection<Module> modules) {
    this.initModules.addAll(modules);
  }

  /**
   * Allows subclasses to exclude components that are present on their classpath but not meant to be used by the
   * application. Especially useful for tests where the classpath is not specific to just one app.
   */
  protected boolean acceptComponent(Class<?> type) {
    return true;
  }

  private <Q extends Annotation, C> Iterable<BeanEntry<Q, C>> filter(Iterable<? extends BeanEntry<Q, C>> iterable) {
    List<BeanEntry<Q, C>> components = new ArrayList<BeanEntry<Q, C>>();
    for (BeanEntry<Q, C> entry : iterable) {
      if (acceptComponent(entry.getImplementationClass())) {
        components.add(entry);
      }
    }
    return components;
  }

  private void addManaged(Environment environment, BeanLocator locator) {
    for (BeanEntry<Annotation, Managed> managedBeanEntry : filter(locator.locate(Key.get(Managed.class)))) {
      Managed managed = managedBeanEntry.getValue();
      environment.lifecycle().manage(managed);
      logger.debug("Added managed: {}", managed);
    }
  }

  private void addTasks(Environment environment, BeanLocator locator) {
    for (BeanEntry<Annotation, Task> taskBeanEntry : filter(locator.locate(Key.get(Task.class)))) {
      Task task = taskBeanEntry.getValue();
      environment.admin().addTask(task);
      logger.debug("Added task: {}", task);
    }
  }

  private void addHealthChecks(Environment environment, BeanLocator locator) {
    for (BeanEntry<Annotation, HealthCheck> healthCheckBeanEntry : filter(locator.locate(Key.get(HealthCheck.class)))) {
      HealthCheck healthCheck = healthCheckBeanEntry.getValue();
      environment.healthChecks().register(healthCheck.toString(), healthCheck);
      logger.debug("Added healthCheck: {}", healthCheck);
    }
  }

  private void addInjectableProviders(Environment environment, BeanLocator locator) {
    for (BeanEntry<Annotation, InjectableProvider> injectableProviderBeanEntry : filter(locator.locate(Key
        .get(InjectableProvider.class)))) {
      InjectableProvider injectableProvider = injectableProviderBeanEntry.getValue();
      environment.jersey().register(injectableProvider);
      logger.debug("Added injectableProvider: {}", injectableProvider);
    }
  }

  private void addProviders(Environment environment, BeanLocator locator) {
    for (BeanEntry<Annotation, Provider> providerBeanEntry : filter(locator.locate(Key.get(Provider.class)))) {
      Provider provider = providerBeanEntry.getValue();
      environment.jersey().register(provider);
      logger.debug("Added provider: {}", provider);
    }
  }

  private void addResources(Environment environment, BeanLocator locator) {
    //
    // Unfortunately @Path is not a qualifier in JSR330, so we need to check all known bindings.
    // (In practice this isn't that slow because of various caches in Sisu to optimize lookups.)
    // We could always optimize this by introducing a marker interface for injectable resources.
    //
    logger.debug("Adding resources");
    for (BeanEntry<Annotation, Object> resourceBeanEntry : filter(locator.locate(Key.get(Object.class)))) {
      Class<?> impl = resourceBeanEntry.getImplementationClass();
      if (impl != null && impl.isAnnotationPresent(Path.class)) {
        try {
          /*
           * NOTE: Not using addResource(Object) to avoid https://java.net/jira/browse/JERSEY-692 and not using explicit
           * root resources to avoid https://java.net/jira/browse/JERSEY-2141. Instead, SisuComponentProviderFactory
           * teaches Jersey how to instantiante the resource.
           */
          environment.jersey().register(impl);
          logger.debug("Added resource: {}", impl);
        }
        catch (Exception e) {
          logger.warn("Unable to add resource: {}", impl, e);
        }
      }
    }
  }

  private void addResourceFilterFactories(Environment environment, BeanLocator locator) {
    List<ResourceFilterFactory> resourceFilterFactories = new ArrayList<ResourceFilterFactory>();
    for (BeanEntry<Annotation, ResourceFilterFactory> beanEntry : filter(locator.locate(Key
        .get(ResourceFilterFactory.class)))) {
      ResourceFilterFactory resourceFilterFactory = beanEntry.getValue();
      logger.debug("Added resource filter factory: {}", resourceFilterFactory);
      resourceFilterFactories.add(resourceFilterFactory);
    }
    if (!resourceFilterFactories.isEmpty()) {
      environment.jersey().property(ResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, resourceFilterFactories);
    }
  }

  private static class SisuComponentProviderFactory
      implements IoCComponentProviderFactory
  {
    private final BeanLocator container;

    public SisuComponentProviderFactory(final BeanLocator container) {
      this.container = container;
    }

    @Override
    public IoCComponentProvider getComponentProvider(final Class<?> type) {
      IoCComponentProvider provider = null;

      Iterator<BeanEntry<Annotation, ?>> iter = container.locate(Key.get((Class) type)).iterator();
      if (iter.hasNext()) {
        final BeanEntry entry = iter.next();

        provider = new IoCInstantiatedComponentProvider()
        {
          @Override
          public Object getInjectableInstance(final Object obj) {
            return obj;
          }

          @Override
          public Object getInstance() {
            return entry.getValue();
          }
        };
      }

      return provider;
    }

    @Override
    public IoCComponentProvider getComponentProvider(final ComponentContext context, final Class<?> type) {
      return getComponentProvider(type);
    }
  }
}
