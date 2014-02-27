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

package org.sonatype.sisu.litmus.testsupport.hamcrest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class URLRespondsWithStatusMatcher
    extends TypeSafeMatcher<URL>
{

  private int statusCode = -2; // since -1 means the response was non-http-valid

  private int timeout = 0; // negative timeout would throw iae, default infinite

  private int receivedStatusCode = 0;

  private boolean timedOut = false;

  private Exception e;

  private String urlString;

  public URLRespondsWithStatusMatcher(final int statusCode) {
    this(statusCode, 0);
  }

  public URLRespondsWithStatusMatcher(final int statusCode, final int timeout) {
    if (timeout < 0) {
      throw new IllegalArgumentException("timeout cannot be negative");
    }
    if (statusCode < -1) {
      throw new IllegalArgumentException("status code cannot be less than negative one");
    }
    this.statusCode = statusCode;
    this.timeout = timeout;
  }

  @Override
  public void describeTo(Description description) {
    if (timedOut) {
      description.appendValue(urlString).appendText(" to connect within ").appendValue(timeout).appendText("ms");
    }
    else if (e != null) {
      description.appendValue(urlString).appendText(" to successfully connect");
    }
    else {
      description.appendValue(urlString).appendText(" to respond with ").appendValue(statusCode);
    }
  }


  @Override
  protected void describeMismatchSafely(URL item, Description mismatchDescription) {
    if (timedOut) {
      mismatchDescription.appendText("took longer.");
      if (this.e != null) {
        mismatchDescription.appendText(this.e.getMessage());
      }
    }
    else if (e != null) {
      mismatchDescription.appendText("got ").appendValue(this.e);
      // description.appendText("got exception ").appendValue(e);
    }
    else {
      mismatchDescription.appendText("was ").appendValue(receivedStatusCode);
    }

  }

  @Override
  protected boolean matchesSafely(URL item) {
    this.urlString = item.toString();
    try {
      URL url = new URL(item.toString());
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      if (timeout > -1) {
        urlConnection.setConnectTimeout(timeout);
        urlConnection.setReadTimeout(timeout);
      }
      urlConnection.setUseCaches(false);
      urlConnection.connect();
      this.receivedStatusCode = urlConnection.getResponseCode();
    }
    catch (SocketTimeoutException ste) {
      timedOut = true;
      this.e = ste;
    }
    catch (IOException ioe) {
      this.e = ioe;
    }
    catch (Exception e) {
      this.e = e;
    }
    return (!timedOut && e == null && this.receivedStatusCode == this.statusCode);

  }

}
