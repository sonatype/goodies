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

import java.io.File;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * {@link TestDataRule} UTs.
 *
 * @since 1.4
 */
public class TestDataRuleTest
    extends TestSupport
{

  @Rule
  public TestDataRule underTest = new TestDataRule(util.resolveFile("src/test/uncopied-resources"));

  /**
   * Test that a file is resolved from root directory.
   *
   * @since 1.0
   */
  @Test
  public void resolveFromRoot() {
    File file = underTest.resolveFile("from-root");
    assertThat(file, is(equalTo(util.resolveFile(
        "src/test/uncopied-resources/from-root"
    ))));
  }

  /**
   * Test that a file is resolved from package directory in root directory.
   *
   * @since 1.0
   */
  @Test
  public void resolveFromPackage() {
    File file = underTest.resolveFile("from-package");
    assertThat(file, is(equalTo(util.resolveFile(
        "src/test/uncopied-resources/org/sonatype/sisu/litmus/testsupport/junit/from-package"
    ))));
  }

  /**
   * Test that a file is resolved from some middle package directory in root directory.
   *
   * @since 1.6.1
   */
  @Test
  public void resolveFromMiddlePackage() {
    File file = underTest.resolveFile("from-middle-package");
    assertThat(file, is(equalTo(util.resolveFile(
        "src/test/uncopied-resources/org/sonatype/sisu/litmus/from-middle-package"
    ))));
  }

  /**
   * Test that a file is resolved from class directory in root directory.
   *
   * @since 1.0
   */
  @Test
  public void resolveFromClass() {
    File file = underTest.resolveFile("from-class");
    assertThat(file, is(equalTo(util.resolveFile(
        "src/test/uncopied-resources/" + TestDataRule.asPath(getClass()) + "/from-class"
    ))));
  }

  /**
   * Test that a file is resolved from method directory in root directory.
   *
   * @since 1.0
   */
  @Test
  public void resolveFromMethod() {
    File file = underTest.resolveFile("from-method");
    assertThat(file, is(equalTo(util.resolveFile(
        "src/test/uncopied-resources/" + TestDataRule.asPath(getClass()) + "/resolveFromMethod/from-method"
    ))));
  }

  /**
   * Test that a RuntimeException is thrown when file does not exist.
   *
   * @since 1.6.1
   */
  @Test(expected = RuntimeException.class)
  public void resolveInexistent()
      throws Exception
  {
    underTest.resolveFile("foo");
  }

}
