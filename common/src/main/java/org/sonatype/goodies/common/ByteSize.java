/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.common;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.goodies.common.ByteSize.ByteUnit.BYTES;
import static org.sonatype.goodies.common.ByteSize.ByteUnit.GIGABYTES;
import static org.sonatype.goodies.common.ByteSize.ByteUnit.KILOBYTES;
import static org.sonatype.goodies.common.ByteSize.ByteUnit.MEGABYTES;
import static org.sonatype.goodies.common.ByteSize.ByteUnit.TERABYTES;

/**
 * Representation of a byte size.
 *
 * Supports:
 *
 * <ul>
 * <li>BYTES</li>
 * <li>KILOBYTES</li>
 * <li>MEGABYTES</li>
 * <li>GIGABYTES</li>
 * <li>TERABYTES</li>
 * </ul>
 *
 * @since 1.1
 */
public final class ByteSize
{
  public static enum ByteUnit
  {
    BYTES,
    KILOBYTES,
    MEGABYTES,
    GIGABYTES,
    TERABYTES;

    public long asBytes(final long value) {
      switch (this) {
        case BYTES:
          return value;
        case KILOBYTES:
          return value * 1024;
        case MEGABYTES:
          return value * 1024 * 1024;
        case GIGABYTES:
          return value * 1024 * 1024 * 1024;
        case TERABYTES:
          return value * 1024 * 1024 * 1024 * 1024;
        default:
          throw new Error();
      }
    }

    public long asKiloBytes(final long value) {
      switch (this) {
        case BYTES:
          return value / 1024;
        case KILOBYTES:
          return value;
        case MEGABYTES:
          return value * 1024;
        case GIGABYTES:
          return value * 1024 * 1024;
        case TERABYTES:
          return value * 1024 * 1024 * 1024;
        default:
          throw new Error();
      }
    }

    public long asMegaBytes(final long value) {
      switch (this) {
        case BYTES:
          return value / 1024 * 1024;
        case KILOBYTES:
          return value / 1024;
        case MEGABYTES:
          return value;
        case GIGABYTES:
          return value * 1024;
        case TERABYTES:
          return value * 1024 * 1024;
        default:
          throw new Error();
      }
    }

    public long asGigaBytes(final long value) {
      switch (this) {
        case BYTES:
          return value / 1024 * 1024 * 1024;
        case KILOBYTES:
          return value / 1024 * 1024;
        case MEGABYTES:
          return value / 1024;
        case GIGABYTES:
          return value;
        case TERABYTES:
          return value * 1024;
        default:
          throw new Error();
      }
    }

    public long asTeraBytes(final long value) {
      switch (this) {
        case BYTES:
          return value / 1024 * 1024 * 1024 * 1024;
        case KILOBYTES:
          return value / 1024 * 1024 * 1024;
        case MEGABYTES:
          return value / 1024 * 1024;
        case GIGABYTES:
          return value / 1024;
        case TERABYTES:
          return value;
        default:
          throw new Error();
      }
    }
  }

  private final long value;

  private final ByteUnit unit;

  public ByteSize(final long value, final ByteUnit unit) {
    this.value = value;
    this.unit = checkNotNull(unit);
  }

  public long value() {
    return value;
  }

  /**
   * @since 1.2
   */
  public int valueI() {
    return (int) value();
  }

  public ByteUnit unit() {
    return unit;
  }

  public long toBytes() {
    return unit.asBytes(value);
  }

  /**
   * @since 1.2
   */
  public int toBytesI() {
    return (int) toBytes();
  }

  public ByteSize asBytes() {
    return bytes(toBytes());
  }

  public long toKiloBytes() {
    return unit.asKiloBytes(value);
  }

  /**
   * @since 1.2
   */
  public int toKiloBytesI() {
    return (int) toKiloBytes();
  }

  public ByteSize asKiloBytes() {
    return kiloBytes(toKiloBytes());
  }

  public long toMegaBytes() {
    return unit.asMegaBytes(value);
  }

  /**
   * @since 1.2
   */
  public int toMegaBytesI() {
    return (int) toMegaBytes();
  }

  public ByteSize asMegaBytes() {
    return megaBytes(toMegaBytes());
  }

  public long toGigaBytes() {
    return unit.asGigaBytes(value);
  }

  /**
   * @since 1.2
   */
  public int toGigaBytesI() {
    return (int) toGigaBytes();
  }

  public ByteSize asGigaBytes() {
    return gigaBytes(toGigaBytes());
  }

  public long toTeraBytes() {
    return unit.asTeraBytes(value);
  }

  /**
   * @since 1.2
   */
  public int toTeraBytesI() {
    return (int) toTeraBytes();
  }

  public ByteSize asTeraBytes() {
    return teraBytes(toTeraBytes());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    ByteSize that = (ByteSize) obj;
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

  public static ByteSize size(final long value, final ByteUnit unit) {
    return new ByteSize(value, unit);
  }

  public static ByteSize bytes(final long value) {
    return new ByteSize(value, BYTES);
  }

  public static ByteSize kiloBytes(final long value) {
    return new ByteSize(value, KILOBYTES);
  }

  public static ByteSize megaBytes(final long value) {
    return new ByteSize(value, MEGABYTES);
  }

  public static ByteSize gigaBytes(final long value) {
    return new ByteSize(value, GIGABYTES);
  }

  public static ByteSize teraBytes(final long value) {
    return new ByteSize(value, TERABYTES);
  }

  //
  // Parsing
  //

  public static ByteSize parse(final String value) {
    if (value != null) {
      return doParse(value.trim().toLowerCase());
    }
    return null;
  }

  private static class ParseConfig
  {
    final ByteUnit unit;

    final String[] suffixes;

    private ParseConfig(final ByteUnit unit, final String... suffixes) {
      this.unit = unit;
      this.suffixes = suffixes;
    }
  }

  private static final ParseConfig[] PARSE_CONFIGS = {
      new ParseConfig(BYTES, "bytes", "byte", "b"),
      new ParseConfig(KILOBYTES, "kilobytes", "kilobyte", "kib", "kb", "k"),
      new ParseConfig(MEGABYTES, "megabytes", "megabyte", "mib", "mb", "m"),
      new ParseConfig(GIGABYTES, "gigabytes", "gigabyte", "gib", "gb", "g"),
      new ParseConfig(TERABYTES, "terabytes", "terabyte", "tib", "tb", "t"),
  };

  private static ByteSize doParse(final String value) {
    for (ParseConfig config : PARSE_CONFIGS) {
      ByteSize t = extract(value, config.unit, config.suffixes);
      if (t != null) {
        return t;
      }
    }
    throw new RuntimeException("Unable to parse: " + value);
  }

  private static ByteSize extract(final String value, final ByteUnit unit, final String... suffixes) {
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
          return new ByteSize(n, unit);
        }
      }
    }

    // else can not extract
    return null;
  }
}