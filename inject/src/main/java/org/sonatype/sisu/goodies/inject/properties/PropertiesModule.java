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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.sonatype.guice.bean.binders.ParameterKeys;
import org.sonatype.sisu.goodies.common.Properties2;
import org.sonatype.sisu.goodies.common.guice.ModuleSupport;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Properties module.
 *
 * Binds {@link ParameterKeys#PROPERTIES} based on ordered loading of one or more {@link PropertiesSource} components.
 * Sources added last take precedence.
 *
 * Property values can be masked to avoid logging them when DEBUG is enabled.
 *
 * @since 1.5
 */
public class PropertiesModule
    extends ModuleSupport
{
    public static final String MASK = "****";

    private final List<PropertiesSource> sources = Lists.newArrayList();

    private final Set<String> mask = Sets.newHashSet();

    public List<PropertiesSource> getSources() {
        return sources;
    }

    public PropertiesModule source(final PropertiesSource source) {
        checkNotNull(source);
        getSources().add(source);
        return this;
    }

    public Set<String> getMask() {
        return mask;
    }

    public PropertiesModule mask(final String propertyName) {
        checkNotNull(propertyName);
        getMask().add(propertyName);
        return this;
    }

    // TODO: Look at using the style in pluginkits ConfigurationPropertiesModule to allow for early binding

    @Override
    protected void configure() {
        checkState(getSources().size() != 0, "At least one source is required");

        Properties props = new Properties();
        for (PropertiesSource source : getSources()) {
            log.debug("Loading properties from source: {}", source);
            props.putAll(source.properties());
        }

        if (props.isEmpty()) {
            log.warn("No properties loaded");
        }
        else if (log.isDebugEnabled()) {
            log.debug("Properties:");
            for (String key : Properties2.sortKeys(props)) {
                Object value = props.get(key);
                if (mask.contains(key)) {
                    value = MASK;
                }
                log.debug("  {}={}", key, value);
            }
        }

        binder().bind(ParameterKeys.PROPERTIES).toInstance(props);
    }
}
