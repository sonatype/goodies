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
package org.sonatype.sisu.goodies.marshal.internal.jackson2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.sisu.goodies.common.TestAccessible;
import org.sonatype.sisu.goodies.marshal.internal.MarshallerSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <a href="https://github.com/FasterXML/jackson-databind">Jackson 2.x</a> {@link org.sonatype.sisu.goodies.marshal.Marshaller}.
 *
 * @since 1.6
 */
@Named("jackson2")
public class JacksonMarshaller
    extends MarshallerSupport
{
    private final ObjectMapper mapper;

    @Inject
    public JacksonMarshaller( final com.fasterxml.jackson.databind.ObjectMapper mapper ) {
        this.mapper = checkNotNull(mapper);
    }

    @TestAccessible
    public JacksonMarshaller() {
        this(new ObjectMapperProvider().get());
    }

    protected String doMarshal(final Object body) throws IOException {
        return mapper.writeValueAsString(body);
    }

    protected <T> T doUnmarshal(final String marshaled, final Class<T> type) throws IOException {
        return mapper.readValue(marshaled, type);
    }
}
