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

package org.sonatype.sisu.litmus.testsupport.hamcrest;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Log file matcher. Will read every line of a log file and will verify that at least one line is matching. Matching is
 * specific to subclasses.
 *
 * @since 1.4
 */
public abstract class LogFileMatcher
    extends TypeSafeMatcher<File>
    implements NegativeMatcher<File>
{

  /**
   * Log file.
   * Can be null if {@link #matchesSafely(java.io.File)} was not yet called.
   */
  private File logFile;

  /**
   * Line number of log file that matches.
   */
  private int matchingLineNumber;

  /**
   * Line that matched.
   */
  private String matchingLine;

  /**
   * Verifies that at least one line of log file is matching.
   *
   * @param logFile to be matched. Cannot be null.
   * @return true if at least one line are matching, false otherwise
   */
  @Override
  protected final boolean matchesSafely(final File logFile) {
    this.logFile = checkNotNull(logFile);
    Scanner scanner = null;
    try {
      scanner = new Scanner(logFile, "UTF-8");
      matchingLineNumber = 0;
      while (scanner.hasNextLine()) {
        matchingLineNumber++;
        matchingLine = scanner.nextLine();
        if (matchesLine(matchingLine)) {
          return true;
        }
      }
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    finally {
      if (scanner != null) {
        scanner.close();
      }
    }
    matchingLineNumber = 0;
    matchingLine = null;
    return false;
  }

  @Override
  public final void describeTo(final Description description) {
    description.appendText("that log file ");
    if (logFile != null) {
      description.appendValue(logFile.getName());
      description.appendText(" ");
    }
    describeTo(logFile, description);
  }

  @Override
  public void describeNegationTo(final Description description) {
    description.appendText("that log file ");
    if (logFile != null) {
      description.appendValue(logFile.getName());
      description.appendText(" ");
    }
    description.appendText("does not ");
    describeTo(logFile, description);
  }

  @Override
  protected final void describeMismatchSafely(final File logFile, final Description mismatchDescription) {
    if (matchingLine != null) {
      mismatchDescription
          .appendText("contained on line ")
          .appendValue(matchingLineNumber)
          .appendText(": ")
          .appendValue(matchingLine);
    }
    else {
      mismatchDescription.appendText("log file ");
      describeMismatchTo(logFile, mismatchDescription);
    }
  }

  /**
   * Subclasses should implement this.
   * Called on each line of log file to be matched.
   *
   * @param line to be matched. Never null.
   * @return true if log line matches, false otherwise
   */
  protected abstract boolean matchesLine(final String line);

  /**
   * Subclasses should implement this and should describe what they are matching.
   *
   * @param logFile     that was matched. Never null.
   * @param description to append to
   */
  protected abstract void describeTo(final File logFile, final Description description);

  /**
   * Subclasses should implement this and should describe the mismatch.
   *
   * @param logFile             that was matched. Never null.
   * @param mismatchDescription to append to
   */
  protected abstract void describeMismatchTo(final File logFile, final Description mismatchDescription);

  /**
   * At least one line of log file should contain the FQN of specified exception class.
   *
   * @param exception to be found in log file. Cannot be null.
   * @return matcher. Never null.
   */
  @Factory
  public static LogFileMatcher hasExceptionOfType(final Class<? extends Exception> exception) {
    checkNotNull(exception);
    return new LogFileMatcher()
    {
      @Override
      public boolean matchesLine(final String line) {
        return line.contains(exception.getName() + ":");
      }

      @Override
      protected void describeTo(final File logFile, final Description description) {
        description.appendText("contains exception of type ").appendValue(exception.getName());
      }

      @Override
      protected void describeMismatchTo(final File logFile, final Description mismatchDescription) {
        mismatchDescription.appendText("did not contain ").appendValue(exception.getName());
      }

    };
  }

  /**
   * At least one line of log file should contain specified text.
   *
   * @param text to be found in log file. Cannot be null.
   * @return matcher
   */
  @Factory
  public static LogFileMatcher hasText(final String text) {
    checkNotNull(text);
    return new LogFileMatcher()
    {
      @Override
      public boolean matchesLine(final String line) {
        return line.contains(text);
      }

      @Override
      protected void describeTo(final File logFile, final Description description) {
        description.appendText("contains ").appendValue(text);
      }

      @Override
      protected void describeMismatchTo(final File logFile, final Description mismatchDescription) {
        mismatchDescription.appendText("did not contain ").appendValue(text);
      }

    };
  }

  /**
   * At least one line of log file should match specified pattern.
   *
   * @param pattern to be found in log file. Cannot be null.
   * @return matcher
   */
  @Factory
  public static LogFileMatcher hasText(final Pattern pattern) {
    return new LogFileMatcher()
    {
      @Override
      public boolean matchesLine(final String line) {
        return pattern.matcher(line).matches();
      }

      @Override
      protected void describeTo(final File logFile, final Description description) {
        description.appendText("matches ").appendValue(pattern);
      }

      @Override
      protected void describeMismatchTo(final File logFile, final Description mismatchDescription) {
        mismatchDescription.appendText("did not match ").appendValue(pattern);
      }

    };
  }

}
