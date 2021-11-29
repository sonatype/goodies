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
package org.sonatype.sisu.goodies.validation.internal;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.sonatype.sisu.goodies.testsupport.inject.InjectedTestSupport;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * {@link ValidationMethodInterceptor} UTs.
 *
 * @since 1.6
 */
public class ValidationMethodInterceptorTest
    extends InjectedTestSupport
{

  @Inject
  private Service serviceWithValidation;

  @Test
  public void callMethodWithoutParams() {
    serviceWithValidation.method1();
  }

  @Test
  public void callMethodWithParamsButNoValidation() {
    serviceWithValidation.method2(new Foo());
  }

  @Test
  public void callMethodWithInvalidParams() {
    try {
      serviceWithValidation.method3(new Foo());
      assertThat("Should had been failed with " + ConstraintViolationException.class.getName(), false);
    }
    catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
      assertThat(violations, is(notNullValue()));
      assertThat(violations.size(), is(1));
    }
  }

  @Test
  public void callMethodWithValidParams() {
    serviceWithValidation.method3(new Foo().withAString("foo"));
  }

  @Test
  public void callMethodWithValidParamsAndInvalidReturn() {
    try {
      serviceWithValidation.method4(new Foo().withAString("foo"));
      assertThat("Should had been failed with " + ConstraintViolationException.class.getName(), false);
    }
    catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
      assertThat(violations, is(notNullValue()));
      assertThat(violations.size(), is(1));
    }
  }

  @Test
  public void callMethodWithInvalidStringParam() {
    try {
      serviceWithValidation.method5(null);
      assertThat("Should had been failed with " + ConstraintViolationException.class.getName(), false);
    }
    catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
      assertThat(violations, is(notNullValue()));
      assertThat(violations.size(), is(1));
    }
  }

  @Test
  public void callMethodWithMultipleInvalidParams1() {
    try {
      serviceWithValidation.method6(null, new Foo());
      assertThat("Should had been failed with " + ConstraintViolationException.class.getName(), false);
    }
    catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
      assertThat(violations, is(notNullValue()));
      assertThat(violations.size(), is(2));
    }
  }

  @Test
  public void callMethodWithMultipleInvalidParams2() {
    try {
      serviceWithValidation.method6(null, null);
      assertThat("Should had been failed with " + ConstraintViolationException.class.getName(), false);
    }
    catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
      assertThat(violations, is(notNullValue()));
      assertThat(violations.size(), is(1));
    }
  }

  @Test
  public void callMethodWithMultipleInvalidParams3() {
    try {
      serviceWithValidation.method7(null, null);
      assertThat("Should had been failed with " + ConstraintViolationException.class.getName(), false);
    }
    catch (final ConstraintViolationException e) {
      final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
      assertThat(violations, is(notNullValue()));
      assertThat(violations.size(), is(2));
    }
  }

}
