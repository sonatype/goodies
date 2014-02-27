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

package org.sonatype.sisu.litmus.testsupport;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Information about the currently executing test.
 *
 * @since 1.3
 */
public interface TestInfo
{

  /**
   * @return the annotation of type annotationType that is attached to the test,
   *         or null if none exists
   */
  <T extends Annotation> T getAnnotation(Class<T> annotationType);

  /**
   * @return all of the annotations attached to the test
   */
  Collection<Annotation> getAnnotations();

  /**
   * @return If this describes a method invocation,
   *         the name of the class of the test instance
   */
  String getClassName();

  /**
   * @return a user-understandable label describing the test
   */
  String getDisplayName();

  /**
   * @return If this describes a method invocation,
   *         the name of the method (or null if not)
   */
  String getMethodName();

  /**
   * @return the class of the test instance, if this describes a method invocation,
   *         .
   */
  Class<?> getTestClass();
}
