/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.testsupport.junit.index;

import javax.annotation.processing.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Test result exchange object.
 *
 * @since 2.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestResult", propOrder = {
    "methodName",
    "duration",
    "success",
    "throwableMessage",
    "throwableStacktrace"
})
@XmlRootElement(name = "testResult")
@Generated(value = "XJC 2.2.5-b10", date = "2018-04-06T11:52:19")
@XStreamAlias("testResult")
public class TestResultXO
{

  @XmlElement(required = true)
  protected String methodName;

  protected long duration;

  protected boolean success;

  protected String throwableMessage;

  protected String throwableStacktrace;

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

  public TestResultXO withMethodName(String value) {
    setMethodName(value);
    return this;
  }

  public TestResultXO withDuration(long value) {
    setDuration(value);
    return this;
  }

  public TestResultXO withSuccess(boolean value) {
    setSuccess(value);
    return this;
  }

  public TestResultXO withThrowableMessage(String value) {
    setThrowableMessage(value);
    return this;
  }

  public TestResultXO withThrowableStacktrace(String value) {
    setThrowableStacktrace(value);
    return this;
  }

}
