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
package org.sonatype.goodies.testsupport;

import java.util.Optional;

import org.sonatype.gossip.Level;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.runners.model.MultipleFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Traces test execution to a {@link Logger}.
 */
public class Test5Tracer
    implements TestWatcher
{
  private static final String UNKNOWN_METHOD_NAME = "UNKNOWN METHOD NAME";

  private final Level level = Level.INFO;

  /**
   * @since litmus 1.3
   */
  protected String prefix(final ExtensionContext context) {
    return format("TEST %s", context.getDisplayName() == null ? UNKNOWN_METHOD_NAME : context.getDisplayName());
  }

  /**
   * @since litmus 1.3
   */
  protected void log(final ExtensionContext context, final String message, final Object... args) {
    level.log(LoggerFactory.getLogger(context.getRequiredTestClass()), message, args);
  }

  @Override
  public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
    log(context, "{} DISABLED {}", prefix(context), reason.orElse(""));
  }

  @Override
  public void testSuccessful(final ExtensionContext context) {
    log(context, "{} SUCCEEDED", prefix(context));
  }

  @Override
  public void testAborted(final ExtensionContext context, final Throwable cause) {
    log(context, "{} ABORTED", prefix(context), cause);
  }

  @Override
  public void testFailed(final ExtensionContext context, final Throwable e) {
    if (e instanceof MultipleFailureException mfe) {
      log(context, "{} FAILED {} {}", prefix(context), e, mfe.getFailures());
    }
    else {
      log(context, "{} FAILED", prefix(context), e);
    }
  }
}
