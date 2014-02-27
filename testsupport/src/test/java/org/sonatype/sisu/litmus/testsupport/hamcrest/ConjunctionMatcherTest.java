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

import java.util.Collection;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * {@link ConjunctionMatcher} UTs.
 *
 * @since 1.4
 */
public class ConjunctionMatcherTest
    extends TestSupport
{

  @Rule
  public ExpectedException thrown = ExpectedException.none().handleAssertionErrors();

  /**
   * Verify that a null matcher element is not allowed.
   */
  @Test
  public void nullMatcherElement() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("Matcher element [0] is null");
    assertThat(
        "some string",
        ConjunctionMatcher.allOf((Matcher<String>) null)
    );
  }

  /**
   * Verify that a null matcher array is not allowed.
   */
  @Test
  public void nullMatcherArray() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("matchers");
    assertThat(
        "some string",
        ConjunctionMatcher.allOf((Matcher<String>[]) null)
    );
  }

  /**
   * Verify that a null matcher element is not allowed.
   */
  @Test
  public void nullMatcherElementInCollection() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("Matcher element [0] is null");

    final Collection<Matcher<? super String>> matchers = Lists.newArrayList();
    matchers.add(null);

    assertThat(
        "some string",
        ConjunctionMatcher.allOf(matchers)
    );
  }

  /**
   * Verify that when first matcher fails, first matcher is used to compose mismatch description.
   */
  @Test
  public void firstNotMatching() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(
        Matchers.<String>allOf(
            Matchers.containsString("(first and second)"),
            Matchers.containsString("but: first failed")
        )
    );

    assertThat(
        "some string",
        ConjunctionMatcher.allOf(
            matcher(true, "first"),
            matcher(false, "second")
        )
    );
  }

  /**
   * Verify that when second matcher fails, second matcher is used to compose mismatch description.
   */
  @Test
  public void secondNotMatching() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(
        Matchers.<String>allOf(
            Matchers.containsString("(first and second)"),
            Matchers.containsString("but: second failed")
        )
    );

    assertThat(
        "some string",
        ConjunctionMatcher.allOf(
            matcher(false, "first"),
            matcher(true, "second")
        )
    );
  }

  /**
   * Verify that when first matcher fails, overridden message is used and first matcher is used to compose mismatch
   * description.
   */
  @Test
  public void firstNotMatchingWithOverriddenDescription() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(
        Matchers.<String>allOf(
            Matchers.containsString("Verify first and second"),
            Matchers.containsString("but: first failed")
        )
    );

    assertThat(
        "some string",
        ConjunctionMatcher.allOf(
            "Verify first and second",
            matcher(true, "first"),
            matcher(false, "second")
        )
    );
  }

  /**
   * Verify that when second matcher fails, overridden message is used and second matcher is used to compose mismatch
   * description.
   */
  @Test
  public void secondNotMatchingWithOverriddenDescription() {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(
        Matchers.<String>allOf(
            Matchers.containsString("Verify first and second"),
            Matchers.containsString("but: second failed")
        )
    );

    assertThat(
        "some string",
        ConjunctionMatcher.allOf(
            "Verify first and second",
            matcher(false, "first"),
            matcher(true, "second")
        )
    );
  }

  private Matcher<String> matcher(final boolean shouldFail, final String position) {
    final Matcher<String> mock = Mockito.mock(StringMatcher.class);
    when(mock.matches(Mockito.any())).thenReturn(!shouldFail);
    doAnswer(new Answer()
    {
      @Override
      public Object answer(final InvocationOnMock invocation)
          throws Throwable
      {
        final Description description = (Description) invocation.getArguments()[0];
        description.appendText(position);
        return null;
      }
    }).when(mock).describeTo(Mockito.<Description>any());
    doAnswer(new Answer()
    {
      @Override
      public Object answer(final InvocationOnMock invocation)
          throws Throwable
      {
        final Description description = (Description) invocation.getArguments()[1];
        description.appendText(position + " failed");
        return null;
      }
    }).when(mock).describeMismatch(Mockito.any(), Mockito.<Description>any());

    return mock;
  }

  private static interface StringMatcher
      extends Matcher<String>
  {

  }

}
