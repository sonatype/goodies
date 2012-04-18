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

package org.sonatype.sisu.goodies.common;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

/**
 * Simple substituting format which only deals with {@code %s} placeholders.
 *
 * This is duplicated from Guava's {@link Preconditions#format} and promoted to public access.
 *
 * @since 1.3
 */
public class SimpleFormat
{
    static final String PLACEHOLDER = "%s";

    /**
     * Substitutes each {@code %s} in {@code template} with an argument. These are matched by position - the first {@code %s} gets {@code args[0]},
     * etc. If there are more arguments than placeholders, the unmatched arguments will be appended to the end of the formatted message in square
     * braces.
     *
     * @param template a non-null string containing 0 or more {@code %s} placeholders.
     * @param args     the arguments to be substituted into the message template.
     *                 Arguments are converted to strings using {@link String#valueOf(Object)}.
     *                 Arguments can be null.
     */
    public static String format(String template, final @Nullable Object... args) {
        template = String.valueOf(template); // null -> "null"

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf(PLACEHOLDER, templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }
}