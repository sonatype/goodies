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
package org.sonatype.goodies.httpfixture.runner.junit;

import org.sonatype.goodies.httpfixture.runner.AbstractSuiteConfiguration;

import org.junit.After;
import org.junit.Before;

/**
 * Adapts {@link AbstractSuiteConfiguration} to JUnit annotations.
 */
public abstract class DefaultSuiteConfiguration
    extends AbstractSuiteConfiguration
{
  @Override
  @Before
  public void before() throws Exception {
    super.before();
  }

  @Override
  @After
  public void after() throws Exception {
    super.after();
  }
}
