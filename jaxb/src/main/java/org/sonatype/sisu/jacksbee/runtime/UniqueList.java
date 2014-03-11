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

package org.sonatype.sisu.jacksbee.runtime;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * A {@link List} which only allows unique elements; similar to {@link Set} but typed as {@link List}.
 *
 * @since jacksbee 1.0
 */
@XStreamAlias("unique-list")
public class UniqueList<E>
    extends AbstractList<E>
{
  /**
   * All list operations delegate to this object.
   */
  @XStreamImplicit
  private final List<E> delegate;

  /**
   * Used to provide object uniqueness.
   */
  @XStreamOmitField
  private /*final*/ Set<E> unique = new LinkedHashSet<E>();

  public UniqueList(final List<E> delegate) {
    if (delegate == null) {
      throw new NullPointerException();
    }
    this.delegate = delegate;
  }

  public UniqueList() {
    this(new ArrayList<E>());
  }

  public static <T> UniqueList<T> create() {
    return new UniqueList<T>();
  }

  public static <T> UniqueList<T> create(final Iterable<? extends T> elements) {
    ArrayList<T> tmp = new ArrayList<T>();
    for (T element : elements) {
      tmp.add(element);
    }
    return new UniqueList<T>(tmp);
  }

  @SuppressWarnings({"unused"})
  private Object readResolve() {
    if (delegate == null) {
      return null;
    }
    // Rebuild the uniqueness set
    unique = new LinkedHashSet<E>();
    Iterator<E> iter = delegate.iterator();
    while (iter.hasNext()) {
      E element = iter.next();
      if (unique.contains(element)) {
        // Serialized state contains duplicate, remove it
        iter.remove();
      }
      else {
        unique.add(element);
      }
    }
    return this;
  }

  @Override
  public boolean contains(final Object obj) {
    return unique.contains(obj);
  }

  @Override
  public E get(final int index) {
    return delegate.get(index);
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public E set(final int index, final E element) {
    if (!contains(element)) {
      unique.add(element);
      return delegate.set(index, element);
    }
    return null;
  }

  @Override
  public void add(final int index, final E element) {
    if (!contains(element)) {
      unique.add(element);
      delegate.add(index, element);
    }
  }

  @Override
  public E remove(final int index) {
    E obj = delegate.remove(index);
    unique.remove(obj);
    return obj;
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
  @Override
  public boolean equals(final Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}