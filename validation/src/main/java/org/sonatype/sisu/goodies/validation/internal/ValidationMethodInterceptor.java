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

package org.sonatype.sisu.goodies.validation.internal;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodDescriptor;

import com.google.inject.matcher.AbstractMatcher;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercepts calls to methods that are subject to jsr303 constraints.
 *
 * @since 1.6
 */
public class ValidationMethodInterceptor
    implements MethodInterceptor
{

  private static final Logger LOG = LoggerFactory.getLogger(ValidationMethodInterceptor.class);

  private final ValidatorFactory validatorFactory;

  private final Matcher matcher;

  public ValidationMethodInterceptor() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    matcher = new Matcher();
  }

  @Override
  public Object invoke(final MethodInvocation invocation)
      throws Throwable
  {
    LOG.debug("Validating {}", invocation.getMethod());

    {
      final Set<ConstraintViolation<Object>> violations =
          validatorFactory.getValidator().forExecutables().validateParameters(
              invocation.getThis(), invocation.getMethod(), invocation.getArguments()
          );

      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(
            "Constraint violations on method parameters of " + invocation.getMethod(), violations
        );
      }
    }

    final Object result = invocation.proceed();

    if (!(result instanceof Void)) {
      final Set<ConstraintViolation<Object>> violations =
          validatorFactory.getValidator().forExecutables().validateReturnValue(
              invocation.getThis(), invocation.getMethod(), result
          );

      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(
            "Constraint violations on return value of " + invocation.getMethod(), violations
        );
      }
    }

    return result;
  }

  public Matcher getMatcher() {
    return matcher;
  }

  /**
   * Matches all methods that have jsr303 constraints on parameters of return value.
   */
  public class Matcher
      extends AbstractMatcher<Method>
  {

    @Override
    public boolean matches(final Method method) {
      final BeanDescriptor descriptor = validatorFactory.getValidator().getConstraintsForClass(
          method.getDeclaringClass()
      );
      final MethodDescriptor constraints = descriptor.getConstraintsForMethod(
          method.getName(), method.getParameterTypes()
      );
      if (constraints != null) {
        LOG.debug("Intercepting (for validation purposes) {}", method);
        return true;
      }
      return false;
    }

  }

}
