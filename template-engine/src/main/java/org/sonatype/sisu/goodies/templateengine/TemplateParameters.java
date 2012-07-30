/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.templateengine;

import java.util.Map;

import org.jetbrains.annotations.NonNls;
import com.google.common.collect.Maps;

/**
 * Helper to build template parameters map.
 *
 * @since 1.4
 */
public class TemplateParameters
{

    private final Map<String, Object> params = Maps.newHashMap();

    public TemplateParameters set( final @NonNls String key, final Object value )
    {
        params.put( key, value );
        return this;
    }

    public Map<String, Object> get()
    {
        return params;
    }

}
