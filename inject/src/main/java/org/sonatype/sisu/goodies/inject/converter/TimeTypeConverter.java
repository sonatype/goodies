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

import javax.inject.Named;

import org.sonatype.sisu.goodies.common.Time;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

/**
 * Guice {@link TypeConverter} for {@link Time} instances.
 *
 * @since 1.5
 */
@Named
public class TimeTypeConverter
    extends TypeConverterSupport<Time>
{
  // FIXME: This low-level type converter will not be picked up due to an issue in Guice, pending investigation on fixing it
  // FIXME: For now, any project that needs this component should copy this class into their project :-(

  @Override
  protected Object doConvert(final String value, final TypeLiteral<?> toType) throws Exception {
    return Time.parse(value);
  }
}
