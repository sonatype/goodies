/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper for working with <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601<a/> dates.
 *
 * @since 1.0
 */
public class Iso8601Date
{
  public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; //NON-NLS
  //public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ";
  //public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * @since 1.5
   */
  private Iso8601Date() {}

  private static DateFormat getFormat() {
    return new SimpleDateFormat(PATTERN);
  }

  public static Date parse(final String value) throws ParseException {
    checkNotNull(value);
    return getFormat().parse(value);
  }

  public static String format(final Date date) {
    checkNotNull(date);
    return getFormat().format(date);
  }
}