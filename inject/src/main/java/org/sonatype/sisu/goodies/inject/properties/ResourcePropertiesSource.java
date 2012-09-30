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

package org.sonatype.sisu.goodies.inject.properties;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Load properties from a resource.
 *
 * @since 1.5
 */
public class ResourcePropertiesSource
    extends PropertiesSourceSupport
{
    private final Class owner;

    private final String resourceName;

    // TODO: Probably should expose better control over the CL used to find the resource

    public ResourcePropertiesSource(final @Nullable Class owner, final String resourceName) {
        if (owner == null) {
            this.owner = getClass();
        }
        else {
            this.owner = owner;
        }
        this.resourceName = checkNotNull(resourceName);
    }

    public ResourcePropertiesSource(final String resourceName) {
        this(null, resourceName);
    }

    @Override
    protected Properties loadProperties() throws Exception {
        Properties props = new Properties();

        URL resource = owner.getResource(resourceName);
        if (resource != null) {
            props.putAll(loadProperties(resource));
        }
        else {
            log.warn("Missing resource: {}", resourceName);
        }

        return props;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "owner=" + owner +
            ", resourceName='" + resourceName + '\'' +
            '}';
    }
}