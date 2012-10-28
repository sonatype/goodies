package org.sonatype.sisu.goodies.common;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Exception-safe {@link Iterable} wrapper.
 *
 * @param <T> Element type.
 *
 * @since 1.5
 */
public final class SafeIterable<T>
    implements Iterable<T>
{
    private final Iterable<T> delegate;

    public SafeIterable(final Iterable<T> delegate) {
        this.delegate = checkNotNull(delegate);
    }

    public Iterator<T> iterator() {
        return new SafeIterator<T>(delegate.iterator());
    }
}
