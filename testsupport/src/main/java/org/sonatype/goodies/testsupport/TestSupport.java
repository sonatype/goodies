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
package org.sonatype.goodies.testsupport;

import org.sonatype.gossip.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Support for tests.
 *
 * @since litmus 1.0
 */
public class TestSupport
{

  protected final TestUtil util = new TestUtil(this);

  protected final Logger logger = util.getLog();

  private Level logLevel = Level.INFO;

  @Rule
  public final TestTracer tracer = new TestTracer(this);

  private AutoCloseable mocks;

  @Before
  public void initMocks() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @After
  public void closeMocks() throws Exception {
    if (mocks != null) {
      mocks.close();
    }
  }

  public Level getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(final Level logLevel) {
    this.logLevel = checkNotNull(logLevel);
  }

  protected void log(final String message) {
    logLevel.log(logger, message);
  }

  protected void log(final Object value) {
    logLevel.log(logger, String.valueOf(value));
  }

  protected void log(final String format, final Object... args) {
    logLevel.log(logger, format, args);
  }

  protected void log(final String message, final Throwable cause) {
    logLevel.log(logger, message, cause);
  }

}