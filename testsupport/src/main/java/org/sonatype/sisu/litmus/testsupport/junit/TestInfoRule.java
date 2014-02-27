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

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.sonatype.sisu.litmus.testsupport.TestInfo;

import com.google.common.base.Preconditions;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * A JUnit {@link org.junit.Rule} implementation of {@link TestInfo}, with values derived from {@link Description}
 *
 * @see Description
 * @since 1.3
 */
public class TestInfoRule
    extends TestWatcher
    implements TestInfo
{
  private Description d;

  @Override
  protected void starting(final Description d) {
    this.d = Preconditions.checkNotNull(d);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String getMethodName() {
    return d.getMethodName();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String getDisplayName() {
    return d.getDisplayName();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String getClassName() {
    return d.getClassName();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Class<?> getTestClass() {
    return d.getTestClass();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
    return d.getAnnotation(annotationType);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Collection<Annotation> getAnnotations() {
    return d.getAnnotations();
  }

}
