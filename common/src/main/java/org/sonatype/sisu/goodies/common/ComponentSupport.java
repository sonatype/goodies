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
package org.sonatype.sisu.goodies.common;

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for components.
 *
 * @since 1.0
 */
public class ComponentSupport
{

    /**
     * The protected member is very bad idea, the use of #getLogger() method is encouraged. This member
     * should be private, but it would break backward compatibility, code that already assumes log is
     * protected, and hence, directly accessible.
     *
     * @deprecated Do not use the inherited protected {@code log} member, but the #getLogger() method instead.
     */
    @NonNls
    @Deprecated
    protected final Logger log;

    /**
     * Default constructor that creates logger for component upon instantiation.
     */
    protected ComponentSupport()
    {
        this.log = checkNotNull( createLogger() );
    }

    /**
     * Creates logger instance to be used with component instance. It might be overridden by subclasses to implement
     * alternative logger naming strategy. By default, this method does the "usual" fluff: {@code LoggerFactory.getLogger(getClass())}.
     *
     * @return The Logger instance to be used by component for logging.
     */
    protected Logger createLogger()
    {
        return LoggerFactory.getLogger( getClass() );
    }

    /**
     * Returns the Logger instance of this component. Never returns {@code null}.
     *
     * @return
     */
    protected Logger getLogger()
    {
        return log;
    }

}