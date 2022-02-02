/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for loading resource-based properties file.
 *
 * @since 1.0
 */
public class PropertiesLoader
{
  private final Properties props = new Properties();

  private final Class<?> owner;

  private final String resourceName;

  public PropertiesLoader(final Class<?> owner, final String resourceName) {
    this.owner = checkNotNull(owner);
    this.resourceName = checkNotNull(resourceName);
  }

  public PropertiesLoader(final Object owner, final String resourceName) {
    this(owner.getClass(), resourceName);
  }

  public Properties getProperties() {
    return props;
  }

  public String getResourceName() {
    return resourceName;
  }

  public PropertiesLoader load() {
    try {
      InputStream input = getResource().openStream();
      try {
        props.load(input);
      }
      finally {
        input.close();
      }
    }
    catch (IOException e) {
      throw new Error("Failed to load properties", e);
    }
    return this;
  }

  public URL getResource() {
    String name = getResourceName();
    URL url = owner.getResource(name);
    if (url == null) {
      throw new Error("Unable to load resource: " + name);
    }
    return url;
  }

  public String getValue(final String name, final String defaultValue) {
    String value = getProperties().getProperty(name);
    if (value == null || value.trim().length() == 0) {
      return defaultValue;
    }
    return value;
  }

  public String getValue(final String name) {
    return getValue(name, null);
  }

  @Override
  public String toString() {
    return props.toString();
  }
}