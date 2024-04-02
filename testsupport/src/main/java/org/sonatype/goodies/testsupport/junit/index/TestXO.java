/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
import javax.annotation.processing.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Test exchange object.
 *
 * @since 1.10
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Test", propOrder = {
    "index",
    "className",
    "testResults",
    "testInfos"
})
@XmlRootElement(name = "test")
@Generated(value = "XJC 2.2.5-b10", date = "2018-04-06T11:52:19")
@XStreamAlias("test")
public class TestXO
{

  protected int index;

  @XmlElement(required = true)
  protected String className;

  @XmlElement(name = "testResult", namespace = "http://sonatype.com/xsd/litmus-testsupport/index/1.1")
  protected List<TestResultXO> testResults;

  @XmlElement(name = "testInfo", namespace = "http://sonatype.com/xsd/litmus-testsupport/index/1.1")
  protected List<TestInfoXO> testInfos;

  public int getIndex() {
    return index;
  }

  public void setIndex(int value) {
    this.index = value;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String value) {
    this.className = value;
  }

  public List<TestResultXO> getTestResults() {
    if (testResults == null) {
      testResults = new ArrayList<TestResultXO>();
    }
    return this.testResults;
  }

  public void setTestResults(List<TestResultXO> value) {
    this.testResults = null;
    List<TestResultXO> draftl = this.getTestResults();
    draftl.addAll(value);
  }

  public List<TestInfoXO> getTestInfos() {
    if (testInfos == null) {
      testInfos = new ArrayList<TestInfoXO>();
    }
    return this.testInfos;
  }

  public void setTestInfos(List<TestInfoXO> value) {
    this.testInfos = null;
    List<TestInfoXO> draftl = this.getTestInfos();
    draftl.addAll(value);
  }

  public TestXO withIndex(int value) {
    setIndex(value);
    return this;
  }

  public TestXO withClassName(String value) {
    setClassName(value);
    return this;
  }

  public TestXO withTestResults(TestResultXO... values) {
    if (values != null) {
      for (TestResultXO value : values) {
        getTestResults().add(value);
      }
    }
    return this;
  }

  public TestXO withTestResults(Collection<TestResultXO> values) {
    if (values != null) {
      getTestResults().addAll(values);
    }
    return this;
  }

  public TestXO withTestResults(List<TestResultXO> value) {
    setTestResults(value);
    return this;
  }

  public TestXO withTestInfos(TestInfoXO... values) {
    if (values != null) {
      for (TestInfoXO value : values) {
        getTestInfos().add(value);
      }
    }
    return this;
  }

  public TestXO withTestInfos(Collection<TestInfoXO> values) {
    if (values != null) {
      getTestInfos().addAll(values);
    }
    return this;
  }

  public TestXO withTestInfos(List<TestInfoXO> value) {
    setTestInfos(value);
    return this;
  }

}
