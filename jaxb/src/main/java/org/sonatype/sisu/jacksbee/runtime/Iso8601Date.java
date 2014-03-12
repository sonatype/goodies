/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.jacksbee.runtime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper for working with <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601<a/> dates.
 *
 * @since jacksbee 1.0
 */
public class Iso8601Date
{
  public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; //NON-NLS
  //public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ";
  //public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  public static final SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN);

  private static DateFormat getFormat() {
    return (DateFormat) FORMAT.clone();
  }

  public static Date parse(final String value) throws ParseException {
    if (value == null) {
      throw new NullPointerException();
    }
    return getFormat().parse(value);
  }

  public static String format(final Date date) {
    if (date == null) {
      throw new NullPointerException();
    }
    return getFormat().format(date);
  }
}