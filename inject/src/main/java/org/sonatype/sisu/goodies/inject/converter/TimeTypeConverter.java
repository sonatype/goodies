/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.inject.converter;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;
import org.sonatype.sisu.goodies.common.Time;

import javax.inject.Named;

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
