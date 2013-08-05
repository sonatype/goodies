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

package org.sonatype.sisu.goodies.marshal.internal;

import org.sonatype.sisu.goodies.common.ComponentSupport;
import org.sonatype.sisu.goodies.marshal.Marshaller;

import com.google.inject.TypeLiteral;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for {@link Marshaller} implementations.
 *
 * @since 1.0
 */
public abstract class MarshallerSupport
    extends ComponentSupport
    implements Marshaller
{
  public String marshal(final Object body) throws Exception {
    checkNotNull(body);

    log.trace("Marshalling: {}", body);

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(body.getClass().getClassLoader());
    try {
      String value = doMarshal(body);
      log.trace("Value: {}", value);

      return value;
    }
    finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  protected abstract String doMarshal(Object body) throws Exception;

  public <T> T unmarshal(final String marshaled, final Class<T> type) throws Exception {
    checkNotNull(marshaled);
    checkNotNull(type);

    log.trace("Unmarshalling {}: {}", type, marshaled);

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(type.getClassLoader());
    try {
      T value = doUnmarshal(marshaled, type);
      log.trace("Value: {}", value);

      return value;
    }
    finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  @SuppressWarnings({"unchecked"})
  public <T> T unmarshal(final String marshaled, final TypeLiteral<T> type) throws Exception {
    checkNotNull(type);
    return (T) unmarshal(marshaled, type.getRawType());
  }

  protected abstract <T> T doUnmarshal(String marshaled, Class<T> type) throws Exception;
}