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

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Hashtable;

/**
 * ???
 *
 * @since 1.5
 */
public class ObjectNameBuilder
{
    private String domain;

    private Hashtable<String,String> properties = new Hashtable<String, String>();

    public ObjectNameBuilder domain(final String domain) {
        this.domain = domain;
        return this;
    }

    public ObjectNameBuilder property(final String key, final String value) {
        properties.put(key, value);
        return this;
    }

    public ObjectName build() throws MalformedObjectNameException {
        return ObjectName.getInstance(domain, properties);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "domain='" + domain + '\'' +
            ", properties=" + properties +
            '}';
    }
}
