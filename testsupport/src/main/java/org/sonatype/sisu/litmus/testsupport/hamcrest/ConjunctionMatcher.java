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

import java.util.Arrays;

import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logical AND between other matchers.
 * <p/>
 * Similar to {@link org.hamcrest.core.AllOf} as functionality yet, will better handle describeTo/describeMismatch.
 *
 * @since 1.4
 */
public class ConjunctionMatcher<T>
    extends TypeSafeMatcher<T>
{

  /**
   * Matchers to be AND-ed.
   * Never null.
   */
  private final Iterable<Matcher<? super T>> matchers;

  /**
   * An alternative description to be used instead of iterating matchers to be AND-ed.
   * Null if description should be composed by iterating matchers.
   */
  private final String alternativeDescription;

  /**
   * First matcher that was failing.
   * Null if matching was not yet performed or all matchers were matching.
   */
  private Matcher<? super T> failingMatcher;

  /**
   * Constructor.
   *
   * @param matchers to be AND-ed. Cannot be null.
   */
  public ConjunctionMatcher(final Iterable<Matcher<? super T>> matchers) {
    this(null, matchers);
  }

  /**
   * Constructor.
   *
   * @param alternativeDescription description to be used instead of iterating over AND-ed matchers.
   * @param matchers               to be AND-ed. Cannot be null.
   */
  public ConjunctionMatcher(@Nullable final String alternativeDescription,
                            final Iterable<Matcher<? super T>> matchers)
  {
    checkNotNull(matchers, "matchers");
    checkArgument(matchers.iterator().hasNext(), "At least one matcher");
    int i = 0;
    for (Matcher<? super T> matcher : matchers) {
      checkNotNull(matcher, "Matcher element [%s] is null", i);
      i++;
    }

    this.alternativeDescription = alternativeDescription;
    this.matchers = Lists.newArrayList(checkNotNull(matchers, "matchers"));
  }

  @Override
  protected boolean matchesSafely(final T item) {
    for (final Matcher<? super T> matcher : matchers) {
      if (!matcher.matches(item)) {
        failingMatcher = matcher;
        return false;
      }
    }
    return true;
  }

  @Override
  public void describeTo(final Description description) {
    if (alternativeDescription == null) {
      description.appendList("(", " " + "and" + " ", ")", matchers);
    }
    else {
      description.appendText(alternativeDescription);
    }
  }

  @Override
  protected void describeMismatchSafely(final T item, final Description mismatchDescription) {
    if (failingMatcher != null) {
      failingMatcher.describeMismatch(item, mismatchDescription);
    }
    else {
      super.describeMismatchSafely(item, mismatchDescription);
    }
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   *
   * @param matchers to be AND-ed. Cannot be null.
   */
  @Factory
  public static <T> Matcher<T> allOf(final Iterable<Matcher<? super T>> matchers) {
    return new ConjunctionMatcher<T>(matchers);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   *
   * @param matchers to be AND-ed. Cannot be null.
   */
  @Factory
  public static <T> Matcher<T> allOf(final Matcher<? super T>... matchers) {
    return allOf(Arrays.asList(checkNotNull(matchers, "matchers")));
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   *
   * @param alternativeDescription description to be used instead of iterating over AND-ed matchers. Cannot be null.
   * @param matchers               to be AND-ed. Cannot be null.
   */
  @Factory
  public static <T> Matcher<T> allOf(final String alternativeDescription,
                                     final Iterable<Matcher<? super T>> matchers)
  {
    return new ConjunctionMatcher<T>(alternativeDescription, matchers);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   *
   * @param alternativeDescription description to be used instead of iterating over AND-ed matchers. Cannot be null.
   * @param matchers               to be AND-ed. Cannot be null.
   */
  @Factory
  public static <T> Matcher<T> allOf(final String alternativeDescription,
                                     final Matcher<? super T>... matchers)
  {
    return allOf(alternativeDescription, Arrays.asList(checkNotNull(matchers, "matchers")));
  }

}
