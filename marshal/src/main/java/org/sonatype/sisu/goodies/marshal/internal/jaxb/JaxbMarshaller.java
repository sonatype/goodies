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
package org.sonatype.sisu.goodies.marshal.internal.jaxb;

import org.sonatype.sisu.goodies.marshal.Marshaller;
import org.sonatype.sisu.goodies.marshal.internal.MarshallerSupport;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.StringReader;
import java.io.StringWriter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <a href="http://jaxb.java.net/">JAXB</a> {@link Marshaller}.
 *
 * @since 1.0
 */
@Named("jaxb")
public class JaxbMarshaller
    extends MarshallerSupport
{
    private final JaxbComponentFactory factory;

    @Inject
    public JaxbMarshaller(final JaxbComponentFactory factory) {
        this.factory = checkNotNull(factory);
    }

    @Override
    protected String doMarshal(final Object body) throws Exception {
        StringWriter buff = new StringWriter();
        factory.marshallerFor(body.getClass()).marshal(body, buff);
        return buff.toString();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected <T> T doUnmarshal(final String marshaled, final Class<T> type) throws Exception {
        StringReader buff = new StringReader(marshaled);
        return (T) factory.unmarshallerFor(type).unmarshal(new InputSource(buff));
    }
}