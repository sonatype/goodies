/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper for working with <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601<a/> dates.
 *
 * @since 1.0
 */
public class Iso8601Date
{
    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; //NON-NLS
    //public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ";
    //public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN);

    private static DateFormat getFormat() {
        return (DateFormat) FORMAT.clone();
    }

    public static Date parse(final String value) throws ParseException {
        checkNotNull(value);
        return getFormat().parse(value);
    }

    public static String format(final Date date) {
        checkNotNull(date);
        return getFormat().format(date);
    }
}