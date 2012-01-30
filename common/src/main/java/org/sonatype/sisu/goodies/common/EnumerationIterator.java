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