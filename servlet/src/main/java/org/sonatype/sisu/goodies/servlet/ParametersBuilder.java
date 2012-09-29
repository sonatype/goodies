/*
 * Copyright (c) 2007-2012 Sonatype, Inc.  All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/central-secure/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */
package org.sonatype.sisu.goodies.servlet;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Helper to build servlet parameters.
 *
 * @since 1.5
 */
public class ParametersBuilder
{
    private final Map<String, String> params = Maps.newHashMap();

    public ParametersBuilder set(final @NonNls String key, final @Nullable Object value) {
        params.put(key, value != null ? String.valueOf(value) : null);
        return this;
    }

    public Map<String, String> get() {
        return params;
    }
}
