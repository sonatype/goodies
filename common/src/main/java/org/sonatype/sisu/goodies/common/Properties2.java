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

package org.sonatype.sisu.goodies.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonatype.sisu.goodies.common.io.Closer;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static methods for working with {@link Properties} instances.
 *
 * @since 1.0
 */
public class Properties2
{
  /**
   * @since 1.5
   */
  private Properties2() {}

  public static Properties load(final File file) throws IOException {
    checkNotNull(file);

    if (file.exists()) {
      return load(file.toURI().toURL());
    }
    else {
      throw new FileNotFoundException("Could not find file: " + file.getAbsolutePath());
    }
  }

  public static Properties load(final URL url) throws IOException {
    checkNotNull(url);

    Properties result = new Properties();
    Reader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      result.load(reader);
    }
    finally {
      Closer.close(reader);
    }
    return result;
  }

  public static String getSystemProperty(final Class<?> type, final @NonNls String name,
                                         final @Nullable Object defaultValue)
  {
    checkNotNull(type);
    checkNotNull(name);
    if (defaultValue == null) {
      return System.getProperty(type.getName() + "." + name);
    }
    else {
      return System.getProperty(type.getName() + "." + name, String.valueOf(defaultValue));
    }
  }

  public static String getSystemProperty(final Class<?> type, final @NonNls String name) {
    return getSystemProperty(type, name, null);
  }

  @SuppressWarnings({"unchecked"})
  public static Collection<String> sortKeys(final Properties source) {
    checkNotNull(source);
    List keys = Lists.newArrayList(source.keySet());
    Collections.sort(keys);
    return keys;
  }

  public static Collection<String> sortKeys(final Map<String, String> source) {
    checkNotNull(source);
    List<String> keys = Lists.newArrayList(source.keySet());
    Collections.sort(keys);
    return keys;
  }
}