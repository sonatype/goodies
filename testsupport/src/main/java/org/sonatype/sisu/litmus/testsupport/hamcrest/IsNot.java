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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Replacement for {@link org.hamcrest.core.IsNot} that delegates mismatch description to wrapped Matcher if set.
 *
 * @since 1.0
 * @deprecated Use {@link InversionMatcher} instead (which can better describe the negation)
 */
@Deprecated
public class IsNot<T>
    extends BaseMatcher<T>
{

  private final Matcher<T> matcher;

  public IsNot(Matcher<T> matcher) {
    this.matcher = matcher;
  }

  @Override
  public boolean matches(Object arg) {
    return !matcher.matches(arg);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("not ").appendDescriptionOf(matcher);
  }

  @Override
  public void describeMismatch(Object item, Description description) {
    // BUG FIX: get matcher mismatch description in case item does not provide a suitable mismatch description
    if (this.matcher != null) {
      matcher.describeMismatch(item, description);
    }
    else {
      super.describeMismatch(item, description);
    }
  }

  /**
   * Inverts the rule.
   *
   * @deprecated Use {@link InversionMatcher} instead (which can better describe the negation)
   */
  @Factory
  @Deprecated
  public static <T> Matcher<T> not(Matcher<T> matcher) {
    return new IsNot(matcher);
  }

  /**
   * This is a shortcut to the frequently used not(equalTo(x)).
   *
   * For example:  assertThat(cheese, is(not(equalTo(smelly))))
   * vs.  assertThat(cheese, is(not(smelly)))
   *
   * @deprecated Use {@link InversionMatcher} instead (which can better describe the negation)
   */
  @Factory
  @Deprecated
  public static <T> Matcher<T> not(T value) {
    return not(org.hamcrest.Matchers.equalTo(value));
  }
}
