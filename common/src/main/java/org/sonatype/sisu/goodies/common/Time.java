/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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
 * @since 1.0
 */
public class Time
{
    private final long value;

    private final TimeUnit unit;

    public Time(final long value, final TimeUnit unit) {
        this.value = value;
        this.unit = checkNotNull(unit);
    }

    public long getValue() {
        return value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public long toNanos() {
        return unit.toNanos(value);
    }

    public long toMicros() {
        return unit.toMicros(value);
    }

    public long toMillis() {
        return unit.toMillis(value);
    }

    public long toSeconds() {
        return unit.toSeconds(value);
    }

    public long toMinutes() {
        return unit.toMinutes(value);
    }

    public long toHours() {
        return unit.toHours(value);
    }

    public long toDays() {
        return unit.toDays(value);
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
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Time that = (Time) obj;
        return value == that.value && unit == that.unit;
    }

    @Override
    public int hashCode() {
        int result = (int) (value ^ (value >>> 32));
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d %s", value, unit.name());
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
}