/*
 * Copyright (c) 2007-2013 Sonatype, Inc. All rights reserved.
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

package org.sonatype.sisu.litmus.testsupport.mock;

import java.util.Arrays;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Setup a test using Mockito annotations, and validate the mockito usage after the test completes.
 * <p>
 * {@link Mockito#validateMockitoUsage() } is called even if the passed statement triggers a {@link Throwable}. If
 * validation
 * fails then, both {@link Throwable}s are wrapped in a {@link MultipleFailureException}
 *
 * @since 1.3
 */
public class MockitoRule
    implements TestRule
{

  final Object testClass;

  public MockitoRule(Object testClass) {
    checkNotNull(testClass);
    this.testClass = testClass;
  }


  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement()
    {
      @Override
      public void evaluate() throws Throwable {
        MockitoAnnotations.initMocks(testClass);
        Throwable throwable = null;
        try {
          base.evaluate();
        }
        catch (final Throwable t) {
          throwable = t;
          throw t;
        }
        finally {
          try {
            Mockito.validateMockitoUsage();
          }
          catch (final Throwable t) {
            if (throwable != null) {
              throw new MultipleFailureException(Arrays.asList(new Throwable[]{throwable, t}));
            }
            else {
              throw t;
            }
          }
        }
      }
    };
  }
}