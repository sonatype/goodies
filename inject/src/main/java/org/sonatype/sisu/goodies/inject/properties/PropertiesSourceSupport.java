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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.common.io.Closer;

import com.google.common.base.Throwables;

/**
 * Support for {@link PropertiesSource} implementations.
 *
 * @since 1.5
 */
public abstract class PropertiesSourceSupport
    extends ComponentSupport
    implements PropertiesSource
{
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

  private boolean isBuffered(final InputStream input) {
    return input instanceof BufferedInputStream;
  }

  protected Properties loadProperties(InputStream input) throws Exception {
    Properties props = new Properties();
    if (!isBuffered(input)) {
      input = new BufferedInputStream(input);
    }
    try {
      props.load(input);
      return props;
    }
    finally {
      Closer.close(input);
    }
  }

  private boolean isBuffered(final Reader reader) {
    return reader instanceof BufferedReader;
  }

  protected Properties loadProperties(Reader reader) throws Exception {
    Properties props = new Properties();
    if (!isBuffered(reader)) {
      reader = new BufferedReader(reader);
    }
    try {
      props.load(reader);
      return props;
    }
    finally {
      Closer.close(reader);
    }
  }

  protected Properties loadProperties(final File file) throws Exception {
    log.info("Loading properties from: {}", file);

    if (file.exists()) {
      return loadProperties(new FileReader(file));
    }
    else {
      log.warn("Missing properties file: {}", file);
    }

    return new Properties();
  }

  protected Properties loadProperties(final URL resource) throws Exception {
    log.debug("Loading properties from: {}", resource);

    return loadProperties(resource.openStream());
  }
}