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