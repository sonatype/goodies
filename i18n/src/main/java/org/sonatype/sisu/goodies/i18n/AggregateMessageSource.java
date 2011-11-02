/*
 * Copyright (c) 2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/manager/attributions
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.goodies.i18n;

import com.google.common.collect.Lists;
import org.sonatype.sisu.goodies.common.ComponentSupport;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message source which aggregates messages sources in order.
 *
 * @since 1.0
 */
public class AggregateMessageSource
    extends ComponentSupport
    implements MessageSource
{
    private final List<MessageSource> sources = Lists.newArrayList();

    public AggregateMessageSource(final List<MessageSource> sources) {
        checkNotNull(sources);
        this.sources.addAll(sources);
    }

    public AggregateMessageSource(final MessageSource... sources) {
        this(Arrays.asList(sources));
    }

    public List<MessageSource> getSources() {
        return sources;
    }

    @Override
    public String getMessage(final String code) {
        String result = null;

        for (MessageSource source : sources) {
            try {
                result = source.getMessage(code);
                if (result != null) {
                    break;
                }
            }
            catch (ResourceNotFoundException e) {
                log.trace(e.toString(), e);
            }
        }

        if (result == null) {
            throw new ResourceNotFoundException(code);
        }

        return result;
    }

    @Override
    public String getMessage(final String code, final String defaultValue) {
        try {
            return getMessage(code);
        }
        catch (ResourceNotFoundException e) {
            return defaultValue;
        }
    }

    @Override
    public String format(final String code, final Object... args) {
        String result = null;

        for (MessageSource source : sources) {
            try {
                result = source.format(code, args);
                if (result != null) {
                    break;
                }
            }
            catch (ResourceNotFoundException e) {
                log.trace(e.toString(), e);
            }
        }

        if (result == null) {
            throw new ResourceNotFoundException(code);
        }

        return result;
    }
}