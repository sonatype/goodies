/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.common;

import java.util.Enumeration;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to adapt an {@link Enumeration} as an {@link Iterator}.
 *
 * @since 1.0
 */
public class EnumerationIterator
{
    public static <T> Iterator<T> of(final Enumeration<T> n) {
        checkNotNull(n);
        return new Iterator<T>()
        {
            public boolean hasNext() {
                return n.hasMoreElements();
            }

            public T next() {
                return n.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}