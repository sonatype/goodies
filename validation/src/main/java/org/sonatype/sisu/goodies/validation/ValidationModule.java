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
package org.sonatype.sisu.goodies.validation;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.goodies.inject.ModuleSupport;
import org.sonatype.sisu.goodies.validation.internal.ValidationMethodInterceptor;
import com.google.inject.matcher.Matchers;

/**
 * Guice module adding interception of method calls that are subject of jsr303 constraint validation.
 *
 * @since 1.6
 */
@Named
@Singleton
public class ValidationModule
    extends ModuleSupport
{

    @Override
    protected void configure()
    {
        log.debug( "Binding validation module" );

        final ValidationMethodInterceptor interceptor = new ValidationMethodInterceptor();

        bindInterceptor( Matchers.any(), interceptor.getMatcher(), interceptor );
    }

}
