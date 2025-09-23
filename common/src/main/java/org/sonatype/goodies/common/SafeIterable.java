/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.common;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Exception-safe {@link Iterable} wrapper.
 *
 * @param <T> Element type.
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
    return SafeIterator.of(delegate);
  }

  public static <T> SafeIterable<T> of(final Iterable<T> iterable) {
    return new SafeIterable<T>(iterable);
  }
}