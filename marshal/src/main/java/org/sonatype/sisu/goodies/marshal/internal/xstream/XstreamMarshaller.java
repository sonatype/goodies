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
package org.sonatype.sisu.goodies.marshal.internal.xstream;

import org.sonatype.sisu.goodies.common.TestAccessible;
import org.sonatype.sisu.goodies.marshal.Marshaller;
import org.sonatype.sisu.goodies.marshal.internal.MarshallerSupport;
import com.thoughtworks.xstream.XStream;

import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <a href="http://xstream.codehaus.org">XStream</a> {@link Marshaller}.
 *
 * @since 1.0
 */
@Named("xstream")
public class XstreamMarshaller
    extends MarshallerSupport
{
    private final XStream xstream;

    @Inject
    public XstreamMarshaller(final XStream xstream) {
        this.xstream = checkNotNull(xstream);
    }

    @TestAccessible
    public XstreamMarshaller() {
        this(new XStream());
    }

    @Override
    protected String doMarshal(final Object body) throws Exception {
        xstream.processAnnotations(body.getClass());
        return xstream.toXML(body);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected <T> T doUnmarshal(final String marshaled, final Class<T> type) throws Exception {
        xstream.processAnnotations(type);
        return (T) xstream.fromXML(marshaled);
    }
}