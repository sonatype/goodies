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

import org.jetbrains.annotations.NonNls;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.sisu.goodies.common.ByteSize.ByteUnit.BYTES;
import static org.sonatype.sisu.goodies.common.ByteSize.ByteUnit.GIGABYTES;
import static org.sonatype.sisu.goodies.common.ByteSize.ByteUnit.KILOBYTES;
import static org.sonatype.sisu.goodies.common.ByteSize.ByteUnit.MEGABYTES;
import static org.sonatype.sisu.goodies.common.ByteSize.ByteUnit.TERABYTES;

/**
 * Representation of a byte size.
 *
 * Supports:
 *
 * <ul>
 *     <li>BYTES</li>
 *     <li>KILOBYTES</li>
 *     <li>MEGABYTES</li>
 *     <li>GIGABYTES</li>
 *     <li>TERABYTES</li>
 * </ul>
 *
 * @since 1.1
 */
public class ByteSize
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
                    return value * (1024 ^ 2);
                case GIGABYTES:
                    return value * (1024 ^ 3);
                case TERABYTES:
                    return value * (1024 ^ 4);
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
                    return value * (1024 ^ 2);
                case TERABYTES:
                    return value * (1024 ^ 3);
                default:
                    throw new Error();
            }
        }
        
        public long asMegaBytes(final long value) {
            switch (this) {
                case BYTES:
                    return value / (1024 ^ 2);
                case KILOBYTES:
                    return value / 1024;
                case MEGABYTES:
                    return value;
                case GIGABYTES:
                    return value * 1024;
                case TERABYTES:
                    return value * (1024 ^ 2);
                default:
                    throw new Error();
            }
        }
        
        public long asGigaBytes(final long value) {
            switch (this) {
                case BYTES:
                    return value / (1024 ^ 3);
                case KILOBYTES:
                    return value / (1024 ^ 2);
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
                    return value / (1024 ^ 4);
                case KILOBYTES:
                    return value / (1024 ^ 3);
                case MEGABYTES:
                    return value / (1024 ^ 2);
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

    public long getValue() {
        return value;
    }

    public ByteUnit getUnit() {
        return unit;
    }

    public long toBytes() {
        return unit.asBytes(value);
    }

    public long toKiloBytes() {
        return unit.asKiloBytes(value);
    }

    public long toMegaBytes() {
        return unit.asMegaBytes(value);
    }

    public long toGigaBytes() {
        return unit.asGigaBytes(value);
    }

    public long toTeraBytes() {
        return unit.asTeraBytes(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ByteSize that = (ByteSize) obj;
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

    public static ByteSize size(final long value, final ByteUnit unit) {
        return new ByteSize(value, unit);
    }
    
    public static ByteSize bytes(final long value) {
        return new ByteSize(value, BYTES);
    }

    public static ByteSize kilobytes(final long value) {
        return new ByteSize(value, KILOBYTES);
    }

    public static ByteSize megabytes(final long value) {
        return new ByteSize(value, MEGABYTES);
    }

    public static ByteSize gigabytes(final long value) {
        return new ByteSize(value, GIGABYTES);
    }

    public static ByteSize terabytes(final long value) {
        return new ByteSize(value, TERABYTES);
    }

    //
    // Parsing
    //

    /**
     * @since 1.1
     */
    public static ByteSize parse(final @NonNls String value) {
        if (value != null) {
            return doParse(value.trim().toLowerCase());
        }
        return null;
    }

    private static class ParseConfig
    {
        final ByteUnit unit;

        final String[] suffixes;

        private ParseConfig(final ByteUnit unit, final @NonNls String... suffixes) {
            this.unit = unit;
            this.suffixes = suffixes;
        }
    }

    private static final ParseConfig[] PARSE_CONFIGS = {
        new ParseConfig(BYTES, "bytes", "byte", "b"),
        new ParseConfig(KILOBYTES, "kilobytes", "kilobyte", "kb", "k"),
        new ParseConfig(MEGABYTES, "megabytes", "megabyte", "mb", "m"),
        new ParseConfig(GIGABYTES, "gigabytes", "gigabyte", "gb", "g"),
        new ParseConfig(TERABYTES, "terabytes", "terebyte", "tb", "t"),
    };

    private static ByteSize doParse(final @NonNls String value) {
        for (ParseConfig config : PARSE_CONFIGS) {
            ByteSize t = extract(value, config.unit, config.suffixes);
            if (t != null) {
                return t;
            }
        }
        throw new RuntimeException("Unable to parse: " + value);
    }

    private static ByteSize extract(final String value, final ByteUnit unit, final String... suffixes) {
        for (String suffix : suffixes) {
            int i = value.lastIndexOf(suffix);
            if (i != -1) {
                String s = value.substring(0, i).trim();
                long n = Long.parseLong(s);
                return new ByteSize(n, unit);
            }
        }
        return null;
    }
}