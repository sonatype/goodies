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
package com.sonatype.maven.settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class MavenSettingsHelperTest
{
  private Settings settings;

  private Settings load(String file) throws IOException, XmlPullParserException {
    return new SettingsXpp3Reader().read(new FileInputStream(file));
  }

  @Before
  public void setUp() throws IOException, XmlPullParserException {
    settings = load("src/test/resources/proxy-settings.xml");
    assertEquals(3, settings.getProxies().size()); // sanity check
  }

  @Test
  public void testMalformedUrl() throws IOException, XmlPullParserException {
    try {
      MavenSettingsHelper.selectProxy(settings, "not a url");
      fail();
    }
    catch (MalformedURLException expected) {
      // expected
    }
  }

  @Test
  public void testProtocolHttp() throws MalformedURLException {
    assertEquals("http-proxy", MavenSettingsHelper.selectProxy(settings, "http://somehost.com/somepath").getId());
    assertEquals("http-proxy", MavenSettingsHelper.selectProxy(settings, "HTTP://somehost.com/somepath").getId());
  }

  @Test
  public void testProtocolHttps() throws MalformedURLException {
    assertEquals("https-proxy", MavenSettingsHelper.selectProxy(settings, "https://somehost.com/somepath").getId());
    assertEquals("https-proxy", MavenSettingsHelper.selectProxy(settings, "HTTPs://somehost.com/somepath").getId());
  }

  @Test
  public void testNonProxyHost() throws MalformedURLException {
    assertEquals("other-http-proxy", MavenSettingsHelper.selectProxy(settings, "http://ibiblio.org/path").getId());
    assertEquals("http-proxy", MavenSettingsHelper.selectProxy(settings, "http://www.ibiblio.org/path").getId());
    assertEquals("other-http-proxy", MavenSettingsHelper.selectProxy(settings, "http://www.google.com/path").getId());
    assertEquals("http-proxy", MavenSettingsHelper.selectProxy(settings, "http://google.com/path").getId());
  }

  @Test
  public void testNoProxy() throws IOException, XmlPullParserException {
    Settings settings = load("src/test/resources/noproxy-settings.xml");
    assertNull(MavenSettingsHelper.selectProxy(settings, "http://somehost.com/somepath"));
  }
}
