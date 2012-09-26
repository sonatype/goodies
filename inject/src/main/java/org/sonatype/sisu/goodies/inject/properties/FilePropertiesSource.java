package org.sonatype.sisu.goodies.inject.properties;

import java.io.File;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Load properties from a {@link File}.
 *
 * @since 1.5
 */
public class FilePropertiesSource
    extends PropertiesSourceSupport
{
    private final File file;

    public FilePropertiesSource(final File file) {
        this.file = checkNotNull(file);
    }

    public FilePropertiesSource(final String fileName) {
        this(new File(fileName));
    }

    @Override
    protected Properties loadProperties() throws Exception {
        return loadProperties(file);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "file=" + file +
            '}';
    }
}