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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logical negation of a matcher (inversion).
 * <p/>
 * Similar to {@link org.hamcrest.core.IsNot} as functionality yet, it delegates the
 * {@link Matcher#describeTo(org.hamcrest.Description)} to negated matcher so it better describes the intention.
 *
 * @since 1.4
 */
public class InversionMatcher<T>
    extends BaseMatcher<T>
{

  /**
   * Negated matcher.
   * Never null.
   */
  private final NegativeMatcher<T> matcher;

  /**
   * Constructor.
   *
   * @param matcher to be negated. Cannot be null.
   */
  public InversionMatcher(final NegativeMatcher<T> matcher) {
    this.matcher = checkNotNull(matcher);
  }

  @Override
  public boolean matches(final Object item) {
    return !matcher.matches(item);
  }

  /**
   * Delegates to negated matcher {@link NegativeMatcher#describeNegationTo(org.hamcrest.Description)}.
   *
   * @param description to be built or appended to
   */
  @Override
  public void describeTo(final Description description) {
    matcher.describeNegationTo(description);
  }

  /**
   * Delegates to negated matcher {@link Matcher#describeMismatch(Object, org.hamcrest.Description)}.
   *
   * @param item                that the matcher has rejected
   * @param mismatchDescription description to be built or appended to
   */
  @Override
  public void describeMismatch(final Object item, final Description mismatchDescription) {
    matcher.describeMismatch(item, mismatchDescription);
  }

  /**
   * Inverts matching of provided matcher.
   *
   * @param matcher to be negated. Cannot be null.
   */
  @Factory
  public static <T> InversionMatcher<T> not(final NegativeMatcher<T> matcher) {
    return new InversionMatcher<T>(matcher);
  }

}
