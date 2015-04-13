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
package org.sonatype.sisu.goodies.inject.converter;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeConverter;
import org.eclipse.sisu.inject.TypeArguments;

/**
 * Support for {@link TypeConverter} implementations.
 *
 * @since 1.5
 */
public abstract class TypeConverterSupport<T>
    extends AbstractModule
    implements TypeConverter
{
  private boolean bound;

  @Override
  public void configure() {
    if (!bound) {
      // Explicitly bind module instance under a specific sub-type (not Module as Guice forbids that)
      bind(Key.get(TypeConverterSupport.class, Names.named(getClass().getName()))).toInstance(this);
      bound = true;
    }

    // make sure we pick up the right super type argument, i.e. Foo from TypeConverterSupport<Foo>
    final TypeLiteral<?> superType = TypeLiteral.get(getClass()).getSupertype(TypeConverterSupport.class);
    convertToTypes(Matchers.only(TypeArguments.get(superType, 0)), this);
  }

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
