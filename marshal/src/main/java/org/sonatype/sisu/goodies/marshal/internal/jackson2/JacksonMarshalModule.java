/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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

import javax.inject.Singleton;

import org.sonatype.sisu.goodies.marshal.Marshaller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

/**
 * <a href="https://github.com/FasterXML/jackson-databind">Jackson 2.x</a> marshaling Guice module.
 *
 * @since 1.6
 */
public class JacksonMarshalModule
    extends AbstractModule
{
  @Override
  protected void configure() {
    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
    bind(Marshaller.class).to(JacksonMarshaller.class).in(Singleton.class);
    //bind(Marshaller.class).annotatedWith(named("jackson2")).to(JacksonMarshaller.class).in(Singleton.class);
  }
}
