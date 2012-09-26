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

import com.google.common.base.Throwables;
import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.io.Closer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Support for {@link PropertiesSource} implementations.
 *
 * @since 1.5
 */
public abstract class PropertiesSourceSupport
    extends ComponentSupport
    implements PropertiesSource
{
    protected Properties loadFromStream(final InputStream input) throws Exception {
        Properties props = new Properties();
        try {
            props.load(new BufferedInputStream(input));
            return props;
        }
        finally {
            Closer.close(input);
        }
    }

    @Override
    public Properties properties() {
        try {
            return loadProperties();
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    protected abstract Properties loadProperties() throws Exception;

    protected Properties loadProperties(final File file) throws Exception {
        Properties props = new Properties();
        log.info("Loading properties from: {}", file);

        if (file.exists()) {
            return loadFromStream(new FileInputStream(file));
        }
        else {
            log.warn("Missing properties file: {}", file);
        }

        return props;
    }

    protected Properties loadProperties(final URL resource) throws Exception {
        log.debug("Loading properties from: {}", resource);

        return loadFromStream(resource.openStream());
    }
}