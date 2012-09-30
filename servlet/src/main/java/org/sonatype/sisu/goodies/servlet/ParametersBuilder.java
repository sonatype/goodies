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
    private final Map<String, String> params = Maps.newLinkedHashMap();

    public ParametersBuilder set(final @NonNls String key, final @Nullable Object value) {
        params.put(key, value != null ? String.valueOf(value) : null);
        return this;
    }

    public Map<String, String> get() {
        return params;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "params=" + params +
            '}';
    }
}
