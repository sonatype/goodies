/*
 * Copyright (c) 2007-2012 Sonatype, Inc.  All rights reserved.
 * Includes the third-party code listed at http://links.sonatype.com/products/central-secure/attributions.
 * "Sonatype" is a trademark of Sonatype, Inc.
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
