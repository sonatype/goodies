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

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Representation of a specific unit of time.
 *
 * Supports:
 *
 * <ul>
 * <li>NANOSECONDS</li>
 * <li>MICROSECONDS</li>
 * <li>MILLISECONDS</li>
 * <li>MINUTES</li>
 * <li>HOURS</li>
 * <li>DAYS</li>
 * </ul>
 *
 * @since 1.0
 */
public final class Time
{
  private final long value;

  // TODO: May want to duplicate TimeUnit so we can add support for > DAYS ?

  private final TimeUnit unit;

  public Time(final long value, final TimeUnit unit) {
    this.value = value;
    this.unit = checkNotNull(unit);
  }

  @Deprecated
  public long getValue() {
    return value;
  }

  /**
   * @since 1.1
   */
  public long value() {
    return value;
  }

  @Deprecated
  public TimeUnit getUnit() {
    return unit;
  }

  /**
   * @since 1.1
   */
  public TimeUnit unit() {
    return unit;
  }

  // TODO: May want to use getNanos() here so that Groovy DSL use ends up like Time.seconds(1).nanos instead of Time.seconds(1).toNanos() ?

  public long toNanos() {
    return unit.toNanos(value);
  }

  /**
   * @since 1.1
   */
  public int toNanosI() {
    return (int) toNanos();
  }

  /**
   * @since 1.1
   */
  public Time asNanos() {
    return nanos(toNanos());
  }

  public long toMicros() {
    return unit.toMicros(value);
  }

  /**
   * @since 1.1
   */
  public int toMicrosI() {
    return (int) toMicros();
  }

  /**
   * @since 1.1
   */
  public Time asMicros() {
    return micros(toMicros());
  }

  public long toMillis() {
    return unit.toMillis(value);
  }

  /**
   * @since 1.1
   */
  public int toMillisI() {
    return (int) toMillis();
  }

  /**
   * @since 1.1
   */
  public Time asMillis() {
    return millis(toMillis());
  }

  public long toSeconds() {
    return unit.toSeconds(value);
  }

  /**
   * @since 1.1
   */
  public int toSecondsI() {
    return (int) toSeconds();
  }

  /**
   * @since 1.1
   */
  public Time asSeconds() {
    return seconds(toSeconds());
  }

  public long toMinutes() {
    return unit.toMinutes(value);
  }

  /**
   * @since 1.1
   */
  public int toMinutesI() {
    return (int) toMinutes();
  }

  /**
   * @since 1.1
   */
  public Time asMinutes() {
    return minutes(toMinutes());
  }

  public long toHours() {
    return unit.toHours(value);
  }

  /**
   * @since 1.1
   */
  public int toHoursI() {
    return (int) toHours();
  }

  /**
   * @since 1.1
   */
  public Time asHours() {
    return hours(toHours());
  }

  public long toDays() {
    return unit.toDays(value);
  }

  /**
   * @since 1.1
   */
  public int toDaysI() {
    return (int) toDays();
  }

  /**
   * @since 1.1
   */
  public Time asDays() {
    return days(toDays());
  }

  public void sleep() throws InterruptedException {
    unit.sleep(value);
  }

  public void wait(final Object obj) throws InterruptedException {
    checkNotNull(obj);
    unit.timedWait(obj, value);
  }

  public void join(final Thread thread) throws InterruptedException {
    checkNotNull(thread);
    unit.timedJoin(thread, value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    Time that = (Time) obj;
    return value == that.value && unit == that.unit;
  }

  @Override
  public int hashCode() {
    int result = (int) (value ^ (value >>> 32));
    result = 31 * result + (unit != null ? unit.hashCode() : 0);
    return result;
  }

  private String unitName() {
    // TODO: i18n support?
    String name = unit.name().toLowerCase();
    if (value == 1) {
      name = name.substring(0, name.length() - 1);
    }
    return name;
  }

  @Override
  public String toString() {
    return String.format("%d %s", value, unitName());
  }

  public static Time time(final long value, final TimeUnit unit) {
    return new Time(value, unit);
  }

  public static Time nanos(final long value) {
    return new Time(value, NANOSECONDS);
  }

  public static Time micros(final long value) {
    return new Time(value, MICROSECONDS);
  }

  public static Time millis(final long value) {
    return new Time(value, MILLISECONDS);
  }

  public static Time seconds(final long value) {
    return new Time(value, SECONDS);
  }

  public static Time minutes(final long value) {
    return new Time(value, MINUTES);
  }

  public static Time hours(final long value) {
    return new Time(value, HOURS);
  }

  public static Time days(final long value) {
    return new Time(value, DAYS);
  }

  //
  // Parsing
  //

  /**
   * @since 1.1
   */
  public static Time parse(final String value) {
    if (value != null) {
      return doParse(value.trim().toLowerCase());
    }
    return null;
  }

  private static class ParseConfig
  {
    final TimeUnit unit;

    final String[] suffixes;

    private ParseConfig(final TimeUnit unit, final String... suffixes) {
      this.unit = unit;
      this.suffixes = suffixes;
    }
  }

  private static final ParseConfig[] PARSE_CONFIGS = {
      new ParseConfig(SECONDS, "seconds", "second", "sec", "s"),
      new ParseConfig(MINUTES, "minutes", "minute", "min", "m"),
      new ParseConfig(HOURS, "hours", "hour", "hr", "h"),
      new ParseConfig(DAYS, "days", "day", "d"),

      // These probably used less, so parse last
      new ParseConfig(MILLISECONDS, "milliseconds", "millisecond", "millis", "ms"),
      new ParseConfig(NANOSECONDS, "nanoseconds", "nanosecond", "nanos", "ns"),
      new ParseConfig(MICROSECONDS, "microseconds", "microsecond", "micros", "us"),
  };

  private static Time doParse(final String value) {
    for (ParseConfig config : PARSE_CONFIGS) {
      Time t = extract(value, config.unit, config.suffixes);
      if (t != null) {
        return t;
      }
    }
    throw new RuntimeException("Unable to parse: " + value);
  }

  private static Time extract(final String value, final TimeUnit unit, final String... suffixes) {
    String number=null, units=null;

    for (int p=0; p<value.length(); p++) {
      // skip until we find a non-digit
      if (Character.isDigit(value.charAt(p))) {
        continue;
      }
      // split number and units suffix string
      number = value.substring(0, p);
      units = value.substring(p, value.length()).trim();
      break;
    }

    // if decoded units, check if its one of the supported suffixes
    if (units != null) {
      for (String suffix : suffixes) {
        if (suffix.equals(units)) {
          long n = Long.parseLong(number.trim());
          return new Time(n, unit);
        }
      }
    }

    // else can not extract
    return null;
  }
}