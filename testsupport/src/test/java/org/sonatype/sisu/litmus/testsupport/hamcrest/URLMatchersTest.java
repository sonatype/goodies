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

import java.net.MalformedURLException;
import java.net.URL;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;


/**
 * Tests for {@link URLMatchers}
 *
 * @since 1.8.1
 */
public class URLMatchersTest
{

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(0);

  private ResponseDefinitionBuilder baseResponse() {
    return aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/plain")
        .withBody("<response>Some content</response>");
  }

  @Test
  public void respondsWithStatus200() throws MalformedURLException {
    givenThat(get(urlMatching(".*"))
        .willReturn(baseResponse()));
    assertThat(new URL("http", "localhost", wireMockRule.port(), "/index"), URLMatchers.respondsWithStatus(200));
  }

  @Test
  public void responseTimesOut() throws MalformedURLException {
    givenThat(get(urlMatching(".*"))
        .willReturn(baseResponse().withFixedDelay(2000)));
    assertThat(new URL("http", "localhost", wireMockRule.port(), "/index"),
        not(URLMatchers.respondsWithStatusWithin(200, 1000)));
  }

  @Test
  public void responseWaitsLongEnoughBeforeTimeout() throws MalformedURLException {
    givenThat(get(urlMatching(".*"))
        .willReturn(baseResponse().withFixedDelay(750)));

    assertThat(new URL("http", "localhost", wireMockRule.port(), "/index"),
        URLMatchers.respondsWithStatusWithin(200, 1000));
  }

  /**
   * Example using an assumption to verify that a remote url is available.
   *
   * If it is not, then the test should not fail - instead be ignored by the default runner.
   */
  @Test(expected = AssumptionViolatedException.class)
  public void assumeThatURLDoesNotRespond() throws MalformedURLException {
    assumeThat(new URL("http", "XXXFubarXXX", 8888, "/index"), URLMatchers.respondsWithStatus(200));
    assertThat("test should have halted because we used Assume.assumeThat()", false, is(true));
  }

}
