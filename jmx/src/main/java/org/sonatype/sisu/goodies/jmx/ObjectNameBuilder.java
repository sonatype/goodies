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

package org.sonatype.sisu.goodies.jmx;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Helper to build {@link ObjectName} instances.
 *
 * @since 1.5
 */
public class ObjectNameBuilder
{
    public static final String DOMAIN_SEPARATOR = ":";

    public static final String VALUE_SEPARATOR = "=";

    public static final String STAR = "*";

    public static final String QUESTION = "?";

    private String domain;

    private final Map<String, String> properties = Maps.newLinkedHashMap();

    public ObjectNameBuilder domain(final String domain) {
        this.domain = checkNotNull(domain);
        return this;
    }

    public ObjectNameBuilder domain(final String format, final Object... args) {
        checkNotNull(format);
        return domain(String.format(format, args));
    }

    public ObjectNameBuilder domain() {
        return domain(STAR);
    }

    public ObjectNameBuilder property(final String key, final String value) {
        checkNotNull(key);
        checkNotNull(value);
        properties.put(key, value);
        return this;
    }

    public ObjectNameBuilder property(final String key, final String format, final Object... args) {
        checkNotNull(format);
        return property(key, String.format(format, args));
    }

    public ObjectNameBuilder property(final String key) {
        return property(key, STAR);
    }

    public ObjectNameBuilder property() {
        return property(STAR, STAR);
    }

    public ObjectName build() throws MalformedObjectNameException {
        checkState(domain != null, "Missing domain");
        checkState(!properties.isEmpty(), "Missing properties");

        StringBuilder buff = new StringBuilder();
        buff.append(domain).append(DOMAIN_SEPARATOR);

        Iterator<Entry<String, String>> iter = properties.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            if (STAR.equals(entry.getKey())) {
                buff.append(STAR);
            }
            else {
                buff.append(entry.getKey()).append(VALUE_SEPARATOR).append(entry.getValue());
            }
            if (iter.hasNext()) {
                buff.append(",");
            }
        }

        return ObjectName.getInstance(buff.toString());
    }

    public ObjectName buildQuiet() {
        try {
            return build();
        }
        catch (MalformedObjectNameException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "domain='" + domain + '\'' +
            ", properties=" + properties +
            '}';
    }
}
