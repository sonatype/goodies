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

package org.sonatype.sisu.litmus.testsupport.junit;

import java.util.List;

import junit.framework.Assert;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generic JUnit support utils.
 *
 * @since 1.0
 */
public class JUnitSupport
{

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Run one or more JUnit test classes in the order given, optionally asserting the results.
   * <p/>
   * If {@code assertResults} is true and the result is not successful, failures are logged as per {@link
   * #logFailures(java.util.List)} and {@link junit.framework.AssertionFailedError} is thrown.
   *
   * @param assertResults true will assert that the result of running all of the classes was successful, false ignores
   *                      the result
   * @param testClasses   the test classes to run
   * @throws NullPointerException     if testClasses is null
   * @throws IllegalArgumentException if testClasses are not provided
   * @throws junit.framework.AssertionFailedError
   *                                  if one or more test classes fail
   * @see #logFailures(java.util.List)
   * @see {@link JUnitCore#runClasses(Class[])}}
   * @since 1.3
   */
  public void runUnitTests(final boolean assertResults, final Class... testClasses) {
    if (checkNotNull(testClasses).length == 0) {
      throw new IllegalArgumentException("Please specify one or more JUnit test class.");
    }
    final Result result = JUnitCore.runClasses(testClasses);

    if (assertResults && !result.wasSuccessful()) {
      logFailures(result.getFailures());
      Assert.fail("There were test failures. See logging output.");
    }
  }

  /**
   * Same as calling {@link #runUnitTests(boolean, Class[])} } as {@code runUnitTests(true, testClasses)}.
   *
   * @param testClasses the classes to run as JUnit tests.
   * @see {@link #runUnitTests(boolean, Class[])} }
   * @see {@link JUnitCore#runClasses(Class[])}}
   */
  public void runUnitTests(final Class... testClasses) {
    runUnitTests(true, testClasses);
  }

  /**
   * Logs each failure with stack to ERROR level using this classes's logger.
   *
   * @param failures the failures to log
   * @throws NullPointerException if failures is null
   */
  public void logFailures(final List<Failure> failures) {
    for (final Failure failure : failures) {
      logger.error("Test Failure:", failure.getException());
    }
  }

}
