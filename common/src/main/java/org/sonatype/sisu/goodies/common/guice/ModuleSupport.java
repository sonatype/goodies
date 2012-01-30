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
package org.sonatype.sisu.goodies.common.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for Guice {@link Module} implementations.
 *
 * @since 1.0
 */
public class ModuleSupport
    extends AbstractModule
{
    @NonNls
    protected final Logger log;

    public ModuleSupport() {
        this.log = checkNotNull(createLogger());
    }

    protected Logger createLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    @Override
    protected void configure() {
        // empty
    }

    /**
     * @since 1.2
     */
    protected <T,F> void bindFactory(final Class<T> typeClass, final Class<F> factoryClass) {
        install(new FactoryModuleBuilder()
            .implement(typeClass, typeClass)
            .build(factoryClass));
    }

    /**
     * @since 1.2
     */
    protected <T,F> void bindFactory(final Class<T> typeClass, final Class<? extends T> implClass, final Class<F> factoryClass) {
        install(new FactoryModuleBuilder()
            .implement(typeClass, implClass)
            .build(factoryClass));
    }
}