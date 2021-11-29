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
import java.util.List;

import javax.inject.Named;
import javax.management.MBeanServer;

import org.sonatype.sisu.goodies.testsupport.TestSupport;

import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.junit.Before;
import org.junit.Test;
import org.weakref.jmx.Flatten;
import org.weakref.jmx.MBeanExporter;
import org.weakref.jmx.Managed;
import org.weakref.jmx.Nested;
import org.weakref.jmx.guice.ExportBuilder;
import org.weakref.jmx.guice.MBeanModule;

/**
 * JmxUtils MBeanExporter guice trials.
 */
public class MBeanExporterGuiceTrial
    extends TestSupport
    implements Module
{
  private Injector injector;

  @Named
  public static class TestBean1
      extends SimpleObject
  {
    // empty
  }

  @Named
  public static class TestBean2
      extends SimpleObject
  {
    // empty
  }

  @Named
  public static class TestBean3
  {
    private NamedInteger data;

    @Managed
    @Nested
    public NamedInteger getData() {
      Date date = new Date();
      return new NamedInteger(date.toString(), date.getTime());
    }
  }

  @Named
  public static class TestBean4
  {
    private NamedInteger data;

    @Managed
    @Flatten
    public NamedInteger getData() {
      Date date = new Date();
      return new NamedInteger(date.toString(), date.getTime());
    }
  }

  @Before
  public void setUp() throws Exception {
    List<Module> modules = Lists.newArrayList();
    modules.add(new SpaceModule(new URLClassSpace(getClass().getClassLoader()), BeanScanning.CACHE));
    modules.add(new MBeanModule());
    modules.add(this);
    injector = Guice.createInjector(new WireModule(modules));
  }

  @Override
  public void configure(final Binder binder) {
    binder.bind(MBeanServer.class).toInstance(MBeans.getServer());
    ExportBuilder builder = MBeanModule.newExporter(binder);
    builder.export(TestBean1.class).withGeneratedName();
    builder.export(TestBean2.class).as("test:name=TestBean2");
    builder.export(TestBean3.class).withGeneratedName();
    builder.export(TestBean4.class).withGeneratedName();
  }

  @Test
  public void test() throws Exception {
    MBeanExporter exporter = new MBeanExporter(MBeans.getServer());
    exporter.export("test:name=anotherbean", new TestBean1());
    VisualVmHelper.openCurrentPid().waitFor();
  }
}
