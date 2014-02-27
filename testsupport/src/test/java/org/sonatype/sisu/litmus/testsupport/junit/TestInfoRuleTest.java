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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test {@link org.sonatype.sisu.litmus.testsupport.junit.TestInfoRule}
 *
 * @since 1.3
 */
@TestInfoRuleTest.ValuedAnnotation("test class annotation for testing TestInfoRule.getAnnotation()")
public class TestInfoRuleTest
{

  /**
   * Annotation for testing TestInfoRule.getAnnotation(Class c)
   */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ValuedAnnotation
  {
    String value();
  }

  @Rule
  public TestInfoRule infoRule = new TestInfoRule();

  @ClassRule
  public static TestInfoRule classInfoRule = new TestInfoRule();

  /**
   * The goal here is not to test {@link org.junit.runner.Description}, but that {@link TestInfoRule} derives values
   * from it as we expect
   */
  @Test
  @ValuedAnnotation("test method annotation for testing TestInfoRule.getAnnotation()")
  public void testRule() {
    assertThat(infoRule.getClassName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
    assertThat(infoRule.getMethodName(), equalTo("testRule"));
    assertThat(infoRule.getDisplayName(),
        equalTo("testRule(org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest)"));
    assertThat(infoRule.getTestClass().getCanonicalName(),
        equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
    assertThat(infoRule.getAnnotations(), hasSize(2)); // checking for annotations on this test method
    assertThat(infoRule.getAnnotation(Test.class), notNullValue());
    assertThat(infoRule.getAnnotation(ValuedAnnotation.class).value(),
        equalTo("test method annotation for testing TestInfoRule.getAnnotation()"));
  }

  @Test
  public void testClassRule() {
    assertThat(classInfoRule.getClassName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
    assertThat(classInfoRule.getMethodName(), nullValue());
    assertThat(classInfoRule.getDisplayName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
    assertThat(classInfoRule.getTestClass().getCanonicalName(),
        equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
    assertThat(classInfoRule.getAnnotations(), hasSize(1)); //annotations on this test class
    assertThat(classInfoRule.getAnnotation(ValuedAnnotation.class).value(),
        equalTo("test class annotation for testing TestInfoRule.getAnnotation()"));
  }

}
