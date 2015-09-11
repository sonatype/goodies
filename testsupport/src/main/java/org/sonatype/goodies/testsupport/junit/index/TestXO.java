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
 * Test exchange object.
 *
 * @since 1.10
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Test", propOrder = {
    "index",
    "className",
    "methodName",
    "duration",
    "success",
    "throwableMessage",
    "throwableStacktrace",
    "testInfos"
})
@XmlRootElement(name = "test")
@Generated(value = "XJC 2.2.5-b10", date = "2014-03-10T19:09:54")
@XStreamAlias("test")
public class TestXO
{

  protected int index;

  @XmlElement(required = true)
  protected String className;

  @XmlElement(required = true)
  protected String methodName;

  protected long duration;

  protected boolean success;

  protected String throwableMessage;

  protected String throwableStacktrace;

  @XmlElement(name = "testInfo", namespace = "http://sonatype.com/xsd/litmus-testsupport/index/1.0")
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

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String value) {
    this.methodName = value;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long value) {
    this.duration = value;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean value) {
    this.success = value;
  }

  public String getThrowableMessage() {
    return throwableMessage;
  }

  public void setThrowableMessage(String value) {
    this.throwableMessage = value;
  }

  public String getThrowableStacktrace() {
    return throwableStacktrace;
  }

  public void setThrowableStacktrace(String value) {
    this.throwableStacktrace = value;
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

  public TestXO withMethodName(String value) {
    setMethodName(value);
    return this;
  }

  public TestXO withDuration(long value) {
    setDuration(value);
    return this;
  }

  public TestXO withSuccess(boolean value) {
    setSuccess(value);
    return this;
  }

  public TestXO withThrowableMessage(String value) {
    setThrowableMessage(value);
    return this;
  }

  public TestXO withThrowableStacktrace(String value) {
    setThrowableStacktrace(value);
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
