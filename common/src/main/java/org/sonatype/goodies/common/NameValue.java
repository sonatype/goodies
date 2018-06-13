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

import java.util.Map;
import java.util.List;

import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Container and parser for <tt>name=value</tt> bits.
 *
 * @since 1.1
 */
public class NameValue
{
  public static final String SEPARATOR = "=";

  public static final String TRUE = Boolean.TRUE.toString();

  public final String name;

  public final String value;

  private NameValue(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public static NameValue parse(final String input) {
    checkNotNull(input);
    String name, value;

    int i = input.indexOf(SEPARATOR);
    if (i == -1) {
      name = input;
      value = TRUE;
    }
    else {
      name = input.substring(0, i);
      value = input.substring(i + 1, input.length());
    }

    return new NameValue(name.trim(), value);
  }

  public String toString() {
    return String.format("%s%s'%s'", name, SEPARATOR, value);
  }

  //
  // Decoding
  //

  public static Map<String, String> decode(final String pattern, final String input, final boolean trimValue) {
    checkNotNull(input);

    Map<String, String> parameters = Maps.newLinkedHashMap();
    for (String item : input.split(pattern)) {
      NameValue nv = NameValue.parse(item);
      parameters.put(nv.name, trimValue ? nv.value.trim() : nv.value);
    }
    return parameters;
  }


  public static Map<String, String> decode(final String input, final boolean trimValue) {
    return decode(",|/", input, trimValue);
  }

  public static Map<String, String> decode(final String input) {
    return decode(input, true);
  }
}
