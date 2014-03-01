/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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

import java.util.Collection;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a general identifier for objects.
 *
 * String representation should follow the default behavior of {@link Object#toString}.
 * {@link #hashCode}/{@link #equals} behavior differs to make the OID unique based on
 * {@link #type} and {@link #hash}.  When constructing from an object the hash is always
 * the {@link System#identityHashCode}.
 *
 * @since 1.0
 */
public class OID
{
  public static final OID NULL = new OID();

  public static final String SEPARATOR = "@";

  private final String type;

  private final int hash;

  private OID(final String type, final int hash) {
    this.type = checkNotNull(type);
    this.hash = hash;
  }

  private OID() {
    type = null;
    hash = System.identityHashCode(this);
  }

  public String getType() {
    return type;
  }

  public int getHash() {
    return hash;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof OID)) {
      return false;
    }

    OID that = (OID) obj;
    return Objects.equal(this.type, that.type)
        && Objects.equal(this.hash, that.hash);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, hash);
  }

  @Override
  public String toString() {
    if (this == NULL) {
      return "null";
    }
    return String.format("%s%s%x", type, SEPARATOR, hash);
  }

  public static OID get(final Object obj) {
    if (obj == null) {
      return NULL;
    }
    return new OID(obj.getClass().getName(), System.identityHashCode(obj));
  }

  /**
   * @see #get
   * @since 1.1
   */
  public static OID oid(final Object obj) {
    return get(obj);
  }

  public static OID parse(final String spec) {
    assert spec != null;
    String[] items = spec.split(SEPARATOR);
    if (items.length != 2) {
      throw new IllegalArgumentException();
    }
    return new OID(items[0], Integer.parseInt(items[1], 16));
  }

  public static String render(final Object obj) {
    assert obj != null;
    return get(obj).toString();
  }

  public static <T> T find(final Collection<T> items, final String id) {
    assert items != null;
    assert id != null;

    for (T item : items) {
      if (OID.render(item).equals(id)) {
        return item;
      }
    }

    return null;
  }

  public static <T> T find(final Collection<T> items, final OID id) {
    assert id != null;
    return find(items, id.toString());
  }
}