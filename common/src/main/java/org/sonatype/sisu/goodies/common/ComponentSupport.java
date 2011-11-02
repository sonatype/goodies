/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
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
    @NonNls
    protected final Logger log;

    protected ComponentSupport() {
        this.log = checkNotNull(createLogger());
    }

    protected Logger createLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}