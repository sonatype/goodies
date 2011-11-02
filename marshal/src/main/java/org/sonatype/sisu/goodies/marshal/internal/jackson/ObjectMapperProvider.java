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
package org.sonatype.sisu.goodies.marshal.internal.jackson;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

import javax.inject.Named;
import javax.inject.Provider;

import static org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT;
import static org.codehaus.jackson.map.SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

//import static org.codehaus.jackson.map.DeserializationConfig.Feature.AUTO_DETECT_SETTERS;
//import static org.codehaus.jackson.map.SerializationConfig.Feature.AUTO_DETECT_GETTERS;
//import static org.codehaus.jackson.map.SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS;

/**
 * <a href="http://jackson.codehaus.org">Jackson</a> {@link ObjectMapper} provider.
 *
 * @since 1.0
 */
@Named
public class ObjectMapperProvider
    implements Provider<ObjectMapper>
{
    private final ObjectMapper mapper;

    public ObjectMapperProvider() {
        final ObjectMapper mapper = new ObjectMapper();

        // Configure Jackson annotations only, JAXB annotations can confuse and produce improper content
        DeserializationConfig dconfig = mapper.getDeserializationConfig();
        dconfig.withAnnotationIntrospector(new JacksonAnnotationIntrospector());
        SerializationConfig sconfig = mapper.getSerializationConfig();
        sconfig.withAnnotationIntrospector(new JacksonAnnotationIntrospector());

        // Do not include null values
        sconfig.setSerializationInclusion(NON_NULL);

        // Write dates as ISO-8601
        mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);

        // FIXME: Disable this, as it requires use of @JsonProperty on fields, only required
        // FIXME: ... if object has something that looks like a getter/setter but isn't one

        // Disable detection of setters & getters
        //mapper.configure(AUTO_DETECT_IS_GETTERS, false);
        //mapper.configure(AUTO_DETECT_GETTERS, false);
        //mapper.configure(AUTO_DETECT_SETTERS, false);

        // Make the output look more readable
        mapper.configure(INDENT_OUTPUT, true);

        this.mapper = mapper;
    }

    public ObjectMapper get() {
        return mapper;
    }
}