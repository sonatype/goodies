/*
 * Copyright (c) 2007-2013 Sonatype, Inc. All rights reserved.
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

package org.sonatype.sisu.litmus.testsupport.net;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.google.common.base.Throwables;

/**
 * SSL utility class.
 *
 * @since 1.4
 */
public class SSLUtil
{

  public static void trustEveryone() {
    try {
      SSLContext context = SSLContext.getInstance("SSL");
      TrustManager[] tm = new TrustManager[]{new TrustingX509TrustManager()};
      context.init(null, tm, null);
      SSLContext.setDefault(context);
    }
    catch (Exception e) {
      // don't let checked exceptions through - very unlikely that they happen
      // with above code anyway, and tests using this probably don't want to catch them.
      Throwables.propagate(e);
    }

  }

}
