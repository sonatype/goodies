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
package org.sonatype.goodies.testsupport.hamcrest;

import java.net.URL;

import org.hamcrest.Matcher;

public class URLMatchers
{
  public static Matcher<URL> respondsWithStatus(Integer statusCode) {
    return new URLRespondsWithStatusMatcher(statusCode);
  }

  public static Matcher<URL> respondsWithStatusWithin(int statusCode, int timeoutMillis) {
    return new URLRespondsWithStatusMatcher(statusCode, timeoutMillis);
  }
}
