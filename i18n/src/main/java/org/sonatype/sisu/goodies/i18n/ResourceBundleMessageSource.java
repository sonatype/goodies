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
package org.sonatype.sisu.goodies.i18n;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import org.sonatype.sisu.goodies.common.ComponentSupport;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message source backed up by {@link ResourceBundle} instances.
 *
 * @since 1.0
 */
public class ResourceBundleMessageSource
    extends ComponentSupport
    implements MessageSource
{
    private final List<ResourceBundle> bundles = Lists.newArrayList();

    private final Locale locale;

    public ResourceBundleMessageSource(final Locale locale) {
        this.locale = checkNotNull(locale);
    }

    public ResourceBundleMessageSource(final Class... types) {
        this(Locale.getDefault());
        add(types);
    }

    public Locale getLocale() {
        return locale;
    }

    public ResourceBundleMessageSource add(final boolean required, final Class... types) {
        checkNotNull(types);

        for (Class type : types) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(type.getName(), locale, type.getClassLoader());
                bundles.add(bundle);
            }
            catch (MissingResourceException e) {
                if (required) {
                    throw e;
                }
            }
        }

        return this;
    }

    public ResourceBundleMessageSource add(final Class... types) {
        return add(true, types);
    }

    /**
     * Get a raw message from the resource bundles using the given code.
     */
    @Override
    public String getMessage(final String code) {
        checkNotNull(code);

        for (ResourceBundle bundle : bundles) {
            try {
                return bundle.getString(code);
            }
            catch (MissingResourceException e) {
                log.trace(e.toString(), e);
            }
        }

        throw new ResourceNotFoundException(code);
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

    /**
     * Format a message (based on {@link String#format} using the message
     * from the resource bundles using the given code as a pattern and the
     * given objects as arguments.
     */
    @Override
    public String format(final String code, final @Nullable Object... args) {
        String pattern = getMessage(code);
        if (args != null) {
            return String.format(pattern, args);
        }
        else {
            return pattern;
        }
    }
}
