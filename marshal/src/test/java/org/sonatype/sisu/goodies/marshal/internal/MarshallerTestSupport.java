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
package org.sonatype.sisu.goodies.marshal.internal;

import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;
import org.sonatype.sisu.goodies.marshal.Marshaller;
import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Support for {@link Marshaller} tests.
 */
public class MarshallerTestSupport
    extends TestSupport
{
    protected Marshaller marshaller;

    @After
    public void tearDown() throws Exception {
        marshaller = null;
    }

    public static class TextValue
    {
        protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public TextValue withValue(final String value) {
            this.value = value;
            return this;
        }
    }

    protected TextValue createTextValue() {
        return new TextValue();
    }

    @Test
    public void testMarshal() throws Exception {
        TextValue value1 = createTextValue().withValue("hello");
        String text = marshaller.marshal(value1);
        assertNotNull(text);
        log(text);

        TextValue value2 = marshaller.unmarshal(text, TextValue.class);
        assertNotNull(value2);
        assertEquals(value1.value, value2.value);
    }

    @Test
    public void marshalGenericType() throws Exception {
        List<TextValue> values1 = Lists.newArrayList();
        values1.add(createTextValue().withValue("a"));
        String text = marshaller.marshal(values1);
        assertNotNull(text);
        log(text);

        List<TextValue> values2 = marshaller.unmarshal(text, new TypeLiteral<List<TextValue>>(){});
        assertNotNull(values2);
        log(values2);

        assertEquals(values1.size(), values2.size());
    }
}
