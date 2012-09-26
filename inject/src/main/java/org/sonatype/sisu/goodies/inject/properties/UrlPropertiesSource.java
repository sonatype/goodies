package org.sonatype.sisu.goodies.inject.properties;

import java.net.URL;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Load properties from a {@link URL}.
 *
 * @since 1.5
 */
public class UrlPropertiesSource
    extends PropertiesSourceSupport
{
    private final URL url;

    public UrlPropertiesSource(final URL url) {
        this.url = checkNotNull(url);
    }

    @Override
    protected Properties loadProperties() throws Exception {
        return loadProperties(url);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "url=" + url +
            '}';
    }
}