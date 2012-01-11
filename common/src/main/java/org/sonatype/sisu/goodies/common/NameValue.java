/*
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NonNls;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Container and parser for <tt>name=value</tt> bits.
 *
 * @since 1.1
 */
public class NameValue
{
    @NonNls
    public static final String SEPARATOR = "=";
    
    public static final String TRUE = Boolean.TRUE.toString();

    public final String name;

    public final String value;

    private NameValue(final @NonNls String name, final @NonNls String value) {
        this.name = name;
        this.value = value;
    }

    public static NameValue parse(final @NonNls String input) {
        checkNotNull(input);
        String name, value;

        int i = input.indexOf(SEPARATOR);
        if (i == -1) {
            name = input;
            value = TRUE;
        }
        else {
            name = input.substring(0, i);
            value = input.substring(i + 1, input.length());
        }

        return new NameValue(name.trim(), value);
    }

    public String toString() {
        return String.format("%s%s'%s'", name, SEPARATOR, value);
    }

    //
    // Decoding
    //

    public static Map<String,String> decode(final String pattern, final String input, final boolean trimValue) {
        checkNotNull(input);

        Map<String,String> parameters = Maps.newLinkedHashMap();
        for (String item : input.split(pattern)) {
            NameValue nv = NameValue.parse(item);
            parameters.put(nv.name, trimValue ? nv.value.trim() : nv.value);
        }
        return parameters;
    }


    public static Map<String,String> decode(final String input, final boolean trimValue) {
        return decode(",|/", input, trimValue);
    }

    public static Map<String,String> decode(final String input) {
        return decode(input, true);
    }
}
