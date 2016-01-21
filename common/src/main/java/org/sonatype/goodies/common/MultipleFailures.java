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
package org.sonatype.goodies.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to collect and throw multiple {@link Throwable}s.
 *
 * @since 2.1
 */
public class MultipleFailures
  extends ComponentSupport
{
  private final List<Throwable> failures;

  public MultipleFailures(final int initialCapacity) {
    this.failures = new ArrayList<>(initialCapacity);
  }

  public MultipleFailures() {
    this.failures = new ArrayList<>();
  }

  public List<Throwable> getFailures() {
    return failures;
  }

  public void add(final Throwable failure) {
    log.trace("Adding: {}", failure);
    failures.add(checkNotNull(failure));
  }

  public int size() {
    return failures.size();
  }

  public boolean isEmpty() {
    return failures.isEmpty();
  }

  public class MultipleFailuresException
    extends Exception
  {
    private MultipleFailuresException(@Nullable final String message) {
      super(message);
      for (Throwable failure : failures) {
        addSuppressed(failure);
      }
    }

    public List<Throwable> getFailures() {
      return ImmutableList.copyOf(failures);
    }

    @Override
    public String getMessage() {
      StringBuilder buff = new StringBuilder();

      String message = super.getMessage();
      if (message != null) {
        buff.append(message).append("; ");
      }

      int size = failures.size();
      buff.append(size).append(" ").append(size == 1 ? "failure" : "failures");

      return buff.toString();
    }
  }

  /**
   * Maybe throw {@link MultipleFailuresException} if there are any failures with optional message.
   */
  public void maybePropagate(@Nullable final String message) throws MultipleFailuresException {
    if (!failures.isEmpty()) {
      log.trace("Propagating: {}", failures);
      throw new MultipleFailuresException(message);
    }
  }

  /**
   * Maybe throw {@link MultipleFailuresException} if there are any failures.
   */
  public void maybePropagate() throws MultipleFailuresException {
    maybePropagate(null);
  }
}
