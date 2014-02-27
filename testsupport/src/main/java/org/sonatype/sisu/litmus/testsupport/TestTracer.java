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

import org.sonatype.gossip.Level;

import org.jetbrains.annotations.NonNls;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Traces test execution to a {@link Logger}.
 *
 * @since 1.0
 */
public class TestTracer
    extends TestWatcher
{
  private static final String UNKNOWN_METHOD_NAME = "UNKNOWN METHOD NAME";

  @NonNls
  private final Logger logger;

  @NonNls
  private Level level = Level.INFO;

  public TestTracer(final Logger logger) {
    this.logger = checkNotNull(logger);
  }

  public TestTracer(final Object owner) {
    this(LoggerFactory.getLogger(owner.getClass()));
  }

  public TestTracer withLevel(final Level level) {
    this.level = checkNotNull(level);
    return this;
  }

  /**
   * @since 1.3
   */
  protected String prefix(final Description desc) {
    return format("TEST %s", desc == null ? UNKNOWN_METHOD_NAME : desc.getMethodName());
  }

  /**
   * @since 1.3
   */
  protected void log(String message, Object... args) {
    level.log(logger, message, args);
  }

  @Override
  public void starting(final Description desc) {
    log("{} STARTING", prefix(desc));
  }

  @Override
  public void succeeded(final Description desc) {
    log("{} SUCCEEDED", prefix(desc));
  }

  @Override
  public void failed(final Throwable e, final Description desc) {
    if (e instanceof MultipleFailureException) {
      MultipleFailureException mfe = (MultipleFailureException) e;
      log("{} FAILED {} {}", prefix(desc), e, mfe.getFailures());
    }
    else {
      log("{} FAILED", prefix(desc), e);
    }
  }

  @Override
  public void finished(final Description desc) {
    log("{} FINISHED", prefix(desc));
  }
}