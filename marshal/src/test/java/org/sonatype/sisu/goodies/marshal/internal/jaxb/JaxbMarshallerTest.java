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
package org.sonatype.sisu.goodies.marshal.internal.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.sonatype.sisu.goodies.marshal.Marshaller;
import org.sonatype.sisu.goodies.testsupport.TestSupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link JaxbMarshaller}.
 */
public class JaxbMarshallerTest
    extends TestSupport
{
  private Marshaller marshaller;

  @Before
  public void setUp() throws Exception {
    marshaller = new JaxbMarshaller(new JaxbComponentFactoryImpl());
  }

  @After
  public void tearDown() throws Exception {
    marshaller = null;
  }

  @XmlRootElement
  @XmlType(name = "text-value")
  public static class TextValue
  {
    private String value;

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

  @Test
  public void testMarshal() throws Exception {
    TextValue value1 = new TextValue().withValue("hello");
    String text = marshaller.marshal(value1);
    assertNotNull(text);
    log(text);

    TextValue value2 = marshaller.unmarshal(text, TextValue.class);
    assertNotNull(value2);
    assertEquals(value1.value, value2.value);
  }

  //@Test
  //public void marshalGenericType() throws Exception {
  //    List<TextValue> values1 = Lists.newArrayList();
  //    values1.add(new TextValue().withValue("a"));
  //    String text = marshaller.marshal(values1);
  //    assertNotNull(text);
  //    log(text);
  //
  //    List<TextValue> values2 = marshaller.unmarshal(text, new TypeLiteral<List<TextValue>>(){});
  //    assertNotNull(values2);
  //    log(values2);
  //
  //    assertEquals(values1.size(), values2.size());
  //}
}
