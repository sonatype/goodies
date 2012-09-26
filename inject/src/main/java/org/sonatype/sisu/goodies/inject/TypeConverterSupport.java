/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.sisu.goodies.inject;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;
import org.sonatype.guice.bean.converters.AbstractTypeConverter;

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
