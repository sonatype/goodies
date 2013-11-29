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

package org.sonatype.sisu.goodies.inject.converter;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;
import org.eclipse.sisu.wire.AbstractTypeConverter;

/**
 * Support for {@link TypeConverter} implementations.
 *
 * @since 1.5
 */
public abstract class TypeConverterSupport<T>
    extends AbstractTypeConverter<T>
{
  public Object convert(final String value, final TypeLiteral<?> toType) {
    try {
      return doConvert(value, toType);
    }
    catch (Exception e) {
      throw new ProvisionException(String.format("Unable to convert value: %s due to: %s", value, e)); //NON-NLS
    }
  }

  protected abstract Object doConvert(String value, TypeLiteral<?> toType) throws Exception;
}
