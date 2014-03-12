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

import java.text.ParseException;
import java.util.Date;

/**
 * Adapter to handler <tt>xs:dateTime</tt> as <tt>ISO-8601</tt> dates.
 *
 * @since jacksbee 1.1
 */
public class Iso8601DateTimeAdapter
{
  public static Date parse(final String value) {
    try {
      return Iso8601Date.parse(value);
    }
    catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String print(final Date date) {
    return Iso8601Date.format(date);
  }
}