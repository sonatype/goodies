/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.testsupport.junit.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Test index exchange object.
 *
 * @since 1.10
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Index", propOrder = {
    "counter",
    "tests"
})
@XmlRootElement(name = "index")
@Generated(value = "XJC 2.2.5-b10", date = "2014-03-10T19:09:54")
@XStreamAlias("index")
public class IndexXO
{

  protected int counter;

  @XmlElement(name = "test", namespace = "http://sonatype.com/xsd/litmus-testsupport/index/1.0")
  protected List<TestXO> tests;

  public int getCounter() {
    return counter;
  }

  public void setCounter(int value) {
    this.counter = value;
  }

  public List<TestXO> getTests() {
    if (tests == null) {
      tests = new ArrayList<TestXO>();
    }
    return this.tests;
  }

  public void setTests(List<TestXO> value) {
    this.tests = null;
    List<TestXO> draftl = this.getTests();
    draftl.addAll(value);
  }

  public IndexXO withCounter(int value) {
    setCounter(value);
    return this;
  }

  public IndexXO withTests(TestXO... values) {
    if (values != null) {
      for (TestXO value : values) {
        getTests().add(value);
      }
    }
    return this;
  }

  public IndexXO withTests(Collection<TestXO> values) {
    if (values != null) {
      getTests().addAll(values);
    }
    return this;
  }

  public IndexXO withTests(List<TestXO> value) {
    setTests(value);
    return this;
  }

}
