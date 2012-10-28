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

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Exception-safe {@link Iterator} wrapper.
  *
  * @param <T> Element type.
 *
 * @since 1.5
 */
public final class SafeIterator<T>
    implements Iterator<T>
{
    private final Iterator<T> delegate;

    private T next;

    public SafeIterator(final Iterator<T> delegate) {
        this.delegate = checkNotNull(delegate);
    }

    public boolean hasNext() {
        while (delegate.hasNext()) {
            try {
                next = delegate.next();
                return true;
            }
            catch (final Exception e) {
                continue; // skip bad element
            }
        }
        return false;
    }

    public T next() {
        if (hasNext()) {
            return next;
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static <T> SafeIterator<T> create(final Iterator<T> iterator) {
        return new SafeIterator<T>(iterator);
    }

    public static <T> SafeIterator<T> create(final Iterable<T> iterable) {
        return new SafeIterator<T>(iterable.iterator());
    }
}