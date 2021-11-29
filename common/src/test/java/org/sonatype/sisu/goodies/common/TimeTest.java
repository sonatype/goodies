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
package org.sonatype.sisu.goodies.common;

import java.util.concurrent.TimeUnit;

import org.sonatype.sisu.goodies.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link Time}.
 */
public class TimeTest
    extends TestSupport
{
  @Test(expected = NullPointerException.class)
  public void nullUnit() {
    new Time(1, null);
  }

  @Test
  public void testTimeConversions() {
    Time oneDay = Time.days(1);

    assertEquals(1 * 24 * 60 * 60 * 1000 * 1000L, oneDay.toMicros());
    assertEquals(1 * 24 * 60 * 60 * 1000L, oneDay.toMillis());
    assertEquals(1 * 24 * 60 * 60L, oneDay.toSeconds());
    assertEquals(1 * 24 * 60L, oneDay.toMinutes());
    assertEquals(1 * 24L, oneDay.toHours());
    assertEquals(1L, oneDay.toDays());

    assertEquals(Time.micros(1 * 24 * 60 * 60 * 1000 * 1000L).toDays(), oneDay.value());
    assertEquals(Time.millis(1 * 24 * 60 * 60 * 1000L).toDays(), oneDay.value());
    assertEquals(Time.seconds(1 * 24 * 60 * 60L).toDays(), oneDay.value());
    assertEquals(Time.minutes(1 * 24 * 60L).toDays(), oneDay.value());
    assertEquals(Time.hours(1 * 24L).toDays(), oneDay.value());
    assertEquals(Time.days(1L).toDays(), oneDay.value());
  }

  @Test
  public void parse_nS() throws Exception {
    Time time = Time.parse("1s");
    assertThat(time, equalTo(Time.seconds(1)));
  }

  @Test
  public void parse_n_S() throws Exception {
    Time time = Time.parse("1 s");
    assertThat(time, equalTo(Time.seconds(1)));
  }

  @Test
  public void parse_n_S_() throws Exception {
    Time time = Time.parse("1 s ");
    assertThat(time, equalTo(Time.seconds(1)));
  }

  @Test
  public void parse__n_S_() throws Exception {
    Time time = Time.parse(" 1 s ");
    assertThat(time, equalTo(Time.seconds(1)));
  }

  @Test
  public void parse_nSec() throws Exception {
    Time time = Time.parse("1sec");
    assertThat(time, equalTo(Time.seconds(1)));
  }

  @Test
  public void parse_nM() throws Exception {
    Time time = Time.parse("1m");
    assertThat(time, equalTo(Time.minutes(1)));
  }

  @Test
  public void parse_nMin() throws Exception {
    Time time = Time.parse("1min");
    assertThat(time, equalTo(Time.minutes(1)));
  }

  @Test
  public void asSeconds() {
    Time time = Time.minutes(2).asSeconds();
    log(time);
    assertThat(time.unit(), equalTo(TimeUnit.SECONDS));
    assertThat(time.value(), equalTo((2 * 60L)));
  }

  @Test
  public void toMillisI() throws Exception {
    int n = Time.seconds(1).toMillisI();
    assertThat(n, equalTo(1000));
  }
}