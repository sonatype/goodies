/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.i18n;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * Provides simple access to i18n messages.
 *
 * @since 1.0
 */
public interface MessageSource
{
    /**
     * @throws ResourceNotFoundException
     */
    String getMessage(@NonNls String code);

    String getMessage(@NonNls String code, @Nullable String defaultValue);

    /**
     * @throws ResourceNotFoundException
     */
    String format(@NonNls String code, Object... args);
}
