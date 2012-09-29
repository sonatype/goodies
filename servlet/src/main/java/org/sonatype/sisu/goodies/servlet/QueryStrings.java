/*
 * Copyright (c) 2007-2012 Sonatype, Inc.  All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/central-secure/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
 */

package org.sonatype.sisu.goodies.servlet;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Query-string helpers.
 *
 * @since 1.5
 */
public class QueryStrings
{
    public static Map<String, String> parse(final String input) {
        checkNotNull(input);
        Map<String, String> params = Maps.newHashMap();
        String[] parts = input.split("&");
        for (String part : parts) {
            String[] kv = part.split("=");
            checkState(kv.length == 2, "Malformed parameter");
            params.put(kv[0], kv[1]);
        }
        return params;
    }
}