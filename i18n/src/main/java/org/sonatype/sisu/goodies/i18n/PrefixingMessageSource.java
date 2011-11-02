/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.i18n;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message source which prefixes message codes.
 *
 * @since 1.0
 */
public class PrefixingMessageSource
    implements MessageSource
{
    private final MessageSource messages;

    private final String prefix;

    public PrefixingMessageSource(final MessageSource messages, final String prefix) {
        this.messages = checkNotNull(messages);
        this.prefix = checkNotNull(prefix);
    }

    protected String createCode(final String code) {
        checkNotNull(code);
        return prefix + code;
    }

    @Override
    public String getMessage(final String code) {
        return messages.getMessage(createCode(code));
    }

    @Override
    public String getMessage(final String code, final String defaultValue) {
        return messages.getMessage(createCode(code), defaultValue);
    }

    @Override
    public String format(final String code, final Object... args) {
        return messages.format(createCode(code), args);
    }
}