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
}