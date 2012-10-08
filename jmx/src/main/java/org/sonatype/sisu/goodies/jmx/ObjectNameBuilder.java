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
import java.util.Hashtable;
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
    implements Cloneable
{
    public static final String DOMAIN_SEPARATOR = ":";

    public static final String VALUE_SEPARATOR = "=";

    //public static final String STAR = "*";
    //
    //public static final String QUESTION = "?";

    public static final String TYPE = "type";

    public static final String NAME = "name";

    private String domain;

    private final Map<String, String> properties = Maps.newLinkedHashMap();

    // FIXME: Sort out how to best use domain...
    // FIXME: Should an application have a single domain, and use properties to add hierarchy?
    // FIXME: Or should the domain be used for hierarchy?
    // FIXME: For ref, apache camel and activemq use properties and a single domain
    // FIXME: activemq at least doesn't use standard "type" "name" values, uses "Type" and customer attributes for names
    // FIXME: Really need to sort out the magic used to build the tree in visualvm

    public ObjectNameBuilder domain(final String domain) {
        this.domain = checkNotNull(domain);
        return this;
    }

    /**
     * Set the domain to the given package name.
     */
    public ObjectNameBuilder domain(final Package pkg) {
        checkNotNull(pkg);
        return domain(pkg.getName());
    }

    /**
     * Set the domain to the package name of the given type.
     */
    public ObjectNameBuilder domainOf(final Class type) {
        checkNotNull(type);
        return domain(type.getPackage());
    }

    ///**
    // * Set the domain to {@link #STAR} for queries.
    // */
    //public ObjectNameBuilder domain() {
    //    return domain(STAR);
    //}

    public ObjectNameBuilder property(final String key, final Object value) {
        checkNotNull(key);
        checkNotNull(value);
        properties.put(key, String.valueOf(value));
        return this;
    }

    ///**
    // * Set property to {@link #STAR} for value-pattern queries.
    // */
    //public ObjectNameBuilder property(final String key) {
    //    return property(key, STAR);
    //}
    //
    ///**
    // * Set property to {@link #STAR} for list-pattern queries.
    // */
    //public ObjectNameBuilder property() {
    //    return property(STAR, STAR);
    //}

    /**
     * Set the {@link #TYPE} property to the given value.
     */
    public ObjectNameBuilder type(final Object value) {
        checkNotNull(value);
        property(TYPE, String.valueOf(value));
        return this;
    }

    /**
     * Set the {@link #TYPE} property to the simple-name of the given class.
     */
    public ObjectNameBuilder type(final Class type) {
        checkNotNull(type);
        return type(type.getSimpleName());
    }

    /**
     * Set the {@link #NAME} property to the given value.
     */
    public ObjectNameBuilder name(final Object value) {
        checkNotNull(value);
        return property(NAME, value);
    }

    // TODO: Sort out build() vs. buildQuiet() the later is a bit misleading, as an exception is rethrown not eatten, as one might normally think of as "quiet"

    public ObjectName build() throws MalformedObjectNameException {
        checkState(domain != null, "Missing domain");
        checkState(!properties.isEmpty(), "Missing properties");

        StringBuilder buff = new StringBuilder();
        buff.append(domain).append(DOMAIN_SEPARATOR);

        Iterator<Entry<String, String>> iter = properties.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            //if (STAR.equals(entry.getKey())) {
            //    buff.append(STAR);
            //}
            //else {
            //    buff.append(entry.getKey()).append(VALUE_SEPARATOR).append(entry.getValue());
            //}
            buff.append(entry.getKey()).append(VALUE_SEPARATOR).append(entry.getValue());
            if (iter.hasNext()) {
                buff.append(",");
            }
        }

        return ObjectName.getInstance(buff.toString());
    }

    //public ObjectName build() throws MalformedObjectNameException {
    //    checkState(domain != null, "Missing domain");
    //    checkState(!properties.isEmpty(), "Missing properties");
    //
    //    return ObjectName.getInstance(domain, new Hashtable<String,String>(properties));
    //}

    public ObjectName buildQuiet() {
        try {
            return build();
        }
        catch (MalformedObjectNameException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ObjectNameBuilder copy() {
        try {
            return (ObjectNameBuilder) clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "domain='" + domain + '\'' +
            ", properties=" + properties +
            '}';
    }

    public static String quote(final Object value) {
        checkNotNull(value);
        return ObjectName.quote(String.valueOf(value));
    }
}
