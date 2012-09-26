package org.sonatype.sisu.goodies.inject.properties;

import java.io.File;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Loads properties from a {@link File} where the path is determined by a system property value.
 *
 * @since 1.5
 */
public class SystemPropertyFilePropertiesSource
    extends PropertiesSourceSupport
{
    private final String propertyName;

    public SystemPropertyFilePropertiesSource(final String propertyName) {
        this.propertyName = checkNotNull(propertyName);
    }

    @Override
    protected Properties loadProperties() throws Exception {
        Properties props = new Properties();

        String location = System.getProperty(propertyName);
        if (location != null) {
            props.putAll(loadProperties(new File(location)));
        }
        else {
            log.warn("Missing system property: {}", propertyName);
        }

        return props;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "propertyName='" + propertyName + '\'' +
            '}';
    }
}