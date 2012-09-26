package org.sonatype.sisu.goodies.inject.properties;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Load properties directly from a {@link Properties} instance.
 *
 * @since 1.5
 */
public class DirectPropertiesSource
    extends PropertiesSourceSupport
{
    private final Properties properties;

    public DirectPropertiesSource(final Properties properties) {
        this.properties = checkNotNull(properties);
    }

    @Override
    protected Properties loadProperties() throws Exception {
        return properties;
    }
}
