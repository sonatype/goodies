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
package org.sonatype.sisu.goodies.marshal.internal.jaxb;

import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

/**
 * Default {@link JaxbComponentFactory} implementation.
 *
 * @since 1.0
 */
@Named
public class JaxbComponentFactoryImpl
    implements JaxbComponentFactory
{
    private JAXBContext getContext(final Class type) throws JAXBException {
        return JAXBContext.newInstance(type);
    }

    public Marshaller marshallerFor(final Class type) throws Exception {
        checkNotNull(type);
        Marshaller m = getContext(type).createMarshaller();
        m.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
        return m;
    }

    public Unmarshaller unmarshallerFor(final Class type) throws Exception {
        checkNotNull(type);
        return getContext(type).createUnmarshaller();
    }
}