package org.sonatype.sisu.goodies.inject.properties;

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
    private final String resourceName;

    public ResourcePropertiesSource(final String resourceName) {
        this.resourceName = checkNotNull(resourceName);
    }

    @Override
    protected Properties loadProperties() throws Exception {
        Properties props = new Properties();

        URL resource = getClass().getResource(resourceName);
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
            "resourceName='" + resourceName + '\'' +
            '}';
    }
}