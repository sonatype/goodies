/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.i18n;

/**
 * Thrown to indicate a required resource was not found.
 *
 * @since 1.0
 */
public class ResourceNotFoundException
    extends RuntimeException
{
    public ResourceNotFoundException(final String code) {
        super(String.format("Resource not found for code: %s", code));
    }
}