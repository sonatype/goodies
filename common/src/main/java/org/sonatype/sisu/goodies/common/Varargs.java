/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common;

/**
 * Helper for simplifying use of var-args where <tt>T[]</tt> is required.
 *
 * @since 1.0
 */
public class Varargs
{
    public static <T> T[] va(final T... args) {
        return args;
    }

    public static <T> T[] $(final T... args) {
        return args;
    }
}