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

//import static org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT;

import javax.inject.Named;
import javax.inject.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * <a href="http://jackson.codehaus.org">Jackson</a> {@link org.codehaus.jackson.map.ObjectMapper} provider.
 *
 * @since 1.6
 */
@Named
public class ObjectMapperProvider
    implements Provider<ObjectMapper>
{
    private final ObjectMapper mapper;

    public ObjectMapperProvider() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector( new JacksonAnnotationIntrospector() );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );

        this.mapper = mapper;
    }

    public ObjectMapper get() {
        return mapper;
    }
}
