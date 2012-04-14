/*
 * Copyright (c) 2008-2012 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/pro/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.crypto;

import org.sonatype.sisu.goodies.common.ComponentSupport;

import javax.inject.Inject;
import javax.inject.Named;
import java.security.SecureRandom;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generates random bytes of a specific size.
 *
 * @since 1.3
 */
@Named
public class RandomBytesGenerator
    extends ComponentSupport
{
    private final SecureRandom random;

    @Inject
    public RandomBytesGenerator(final CryptoHelper crypto) {
        this.random = checkNotNull(crypto).createSecureRandom();
    }

    public byte[] generate(final int size) {
        checkArgument(size > 0);
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return bytes;
    }
}
