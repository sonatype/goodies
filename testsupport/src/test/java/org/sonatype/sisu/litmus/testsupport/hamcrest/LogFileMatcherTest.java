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
import java.util.regex.Pattern;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.junit.TestInfoRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * {@link LogFileMatcher} UTs.
 *
 * @since 1.4
 */
public class LogFileMatcherTest
    extends TestSupport
{

  @Rule
  public ExpectedException thrown = ExpectedException.none().handleAssertionErrors();

  @Rule
  public TestInfoRule testInfo = new TestInfoRule();

  /**
   * Verify that log file to be matched does not exist an {@link AssertionError} is thrown with a proper message.
   */
  @Test
  public void inexistentLogFile() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(Matchers.<String>allOf(
        containsString("java.io.FileNotFoundException"),
        containsString("foo.log")
    ));
    assertThat(
        new File("foo.log"),
        LogFileMatcher.hasExceptionOfType(NullPointerException.class)
    );
  }

  /**
   * Verify that a log file that contains an NullPointerException matches.
   */
  @Test
  public void hasNPE() {
    assertThat(
        resolveLogFile(),
        LogFileMatcher.hasExceptionOfType(NullPointerException.class)
    );
  }

  /**
   * Verify that a log file that contains an NullPointerException  and ClassNotFoundException matches.
   */
  @Test
  public void hasNPEAndCNF()
      throws Exception
  {
    assertThat(
        resolveLogFile(),
        Matchers.allOf(
            LogFileMatcher.hasExceptionOfType(NullPointerException.class),
            LogFileMatcher.hasExceptionOfType(ClassNotFoundException.class)
        )
    );
  }

  /**
   * Verifies that a log file that does not have NullPointerException but has ClassNotFoundException matches.
   */
  @Test
  public void hasNoNPE() {
    assertThat(
        resolveLogFile(),
        InversionMatcher.not(LogFileMatcher.hasExceptionOfType(NullPointerException.class))
    );
  }

  /**
   * Verifies that a log file that a text "foo" matches.
   */
  @Test
  public void hasText()
      throws Exception
  {
    assertThat(
        resolveLogFile(),
        LogFileMatcher.hasText("foo")
    );
  }

  /**
   * Verifies that a log file that does not have a text "foo" matches.
   */
  @Test
  public void doesNotHaveText()
      throws Exception
  {
    assertThat(
        resolveLogFile(),
        InversionMatcher.not(LogFileMatcher.hasText("foo"))
    );
  }

  /**
   * Verifies that a log file that has a text "foo-ing" matches pattern "".*foo-ing.*".
   */
  @Test
  public void hasMatchingText()
      throws Exception
  {
    assertThat(
        resolveLogFile(),
        LogFileMatcher.hasText(Pattern.compile(".*foo-ing.*"))
    );
  }

  /**
   * Verifies that a log file that does not have a text "foo-ing" is not matching pattern "".*foo-ing.*".
   */
  @Test
  public void doesNotMatchText()
      throws Exception
  {
    assertThat(
        resolveLogFile(),
        InversionMatcher.not(LogFileMatcher.hasText(Pattern.compile(".*foo-ing.*")))
    );
  }

  private File resolveLogFile() {
    return util.resolveFile(String.format(
        "src/test/uncopied-resources/%s/%s.log", testInfo.getTestClass().getSimpleName(), testInfo.getMethodName()
    ));
  }

}
