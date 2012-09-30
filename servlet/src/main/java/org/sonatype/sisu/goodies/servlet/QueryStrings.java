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

package org.sonatype.sisu.goodies.servlet;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Query-string helpers.
 *
 * @since 1.5
 */
public class QueryStrings
{
    public static final String FIELD_SEPARATOR = "&";

    public static final String VALUE_SEPARATOR = "=";

    /**
     * Parses a query-string into a map.
     *
     * @param input Query-string input ot parse; never null
     * @return  Ordered map of parsed query string parameters.
     */
    public static Map<String, String> parse(final String input) {
        checkNotNull(input);
        Map<String, String> params = Maps.newLinkedHashMap();
        String[] parts = input.split(FIELD_SEPARATOR);
        for (String part : parts) {
            String key, value;
            int i = part.indexOf(VALUE_SEPARATOR);
            if (i == -1) {
                key = part;
                value = null;
            }
            else {
                key = part.substring(0, i);
                value = part.substring(i + 1, part.length());
            }
            params.put(key, value);
        }
        return params;
    }
}