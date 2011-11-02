/*
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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

import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.sisu.goodies.common.TestAccessible;
import org.sonatype.sisu.goodies.i18n.MessageBundle.DefaultMessage;
import org.sonatype.sisu.goodies.i18n.MessageBundle.Key;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Internationalization access.
 *
 * @since 1.0
 */
public class I18N
{
    @NonNls
    private static final Logger log = LoggerFactory.getLogger(I18N.class);

    @TestAccessible
    static final String MISSING_MESSAGE_FORMAT = "ERROR_MISSING_MESSAGE[%s]"; //NON-NLS

    private I18N() {
        super();
    }

    /**
     * Returns a {@link MessageSource} for the given types.
     *
     * @param types     One or more classes
     * @return {@link MessageSource} instance; never null
     */
    public static MessageSource of(final Class... types) {
        checkNotNull(types);
        checkArgument(types.length > 0);
        return new ResourceBundleMessageSource().add(/* bundle is not required */ false, types);
    }

    /**
     * Returns a proxy to the given {@link MessageBundle} type.
     *
     * @return {@link MessageBundle} proxy; never null
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends MessageBundle> T create(final Class<T> type) {
        checkNotNull(type);
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new Handler(type));
    }

    /**
     * Proxy invocation handler to convert method calls into message lookup/format.
     */
    private static class Handler
        implements InvocationHandler
    {
        private final Class<? extends MessageBundle> type;

        private final MessageSource messages;

        public Handler(final Class<? extends MessageBundle> type) {
            this.type = checkNotNull(type);
            this.messages = I18N.of(type);
        }

        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            else if (method.getReturnType() != String.class) {
                throw new Error("Illegal MessageBundle method: " + method);
            }

            // TODO: Optimize/cache

            String key = getKey(method);
            String format = getFormat(key);

            if (format == null) {
                DefaultMessage defaultMessage = method.getAnnotation(DefaultMessage.class);
                if (defaultMessage != null) {
                    format = defaultMessage.value();
                }
            }

            if (format == null) {
                log.warn("Missing message for: {}, key: {}", type, key);
                 return String.format(MISSING_MESSAGE_FORMAT, key);
            }

            if (args != null) {
                // TODO: Support annotation-configuration of formatting method?
                return String.format(format, args);
            }
            return format;
        }

        private String getFormat(final String key) {
            try {
                return messages.getMessage(key);
            }
            catch (ResourceNotFoundException e) {
                log.trace("Missing resource for: {}, key: {}", type, key);
                return null;
            }
        }

        private String getKey(final Method method) {
            Key key = method.getAnnotation(Key.class);
            if (key != null) {
                return key.value();
            }
            return method.getName();
        }
    }
}
