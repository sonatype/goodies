/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
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

package org.sonatype.sisu.goodies.common;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.sonatype.sisu.goodies.common.Preconditions.checkNotNullElements;

/**
 * {@link Preconditions} UTs.
 *
 * @since 1.4
 */
public class PreconditionsTest
    extends TestSupport
{

  private static final Iterable<Object> NULL_REFERENCE = null;

  private static final Iterable<Object> ALL_ELEMENTS_VALID = asList(new Object[]{"1", "2"});

  private static final Iterable<Object> ONE_ELEMENT_NULL = asList(new Object[]{"1", null, "3"});

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Verify that when a null reference is passed in, validation is not performed and no exception is thrown.
   */
  @Test
  public void nullReference() {
    assertThat(checkNotNullElements(NULL_REFERENCE), is(nullValue()));
  }

  /**
   * Verify that when a null reference is passed in, validation is not performed and no exception is thrown.
   */
  @Test
  public void nullReferenceWhenMessage() {
    assertThat(checkNotNullElements(NULL_REFERENCE, "Some message"), is(nullValue()));
  }

  /**
   * Verify that when a null reference is passed in, validation is not performed and no exception is thrown.
   */
  @Test
  public void nullReferenceWhenMessageAndArguments() {
    assertThat(checkNotNullElements(NULL_REFERENCE, "Some message %s", "foo"), is(nullValue()));
  }

  /**
   * Verify that an NPE is thrown and a default message is used.
   */
  @Test
  public void nullElementWhenNoMessage() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("Element at position '1'");

    checkNotNullElements(ONE_ELEMENT_NULL);
  }

  /**
   * Verify that an NPE is thrown and a specified message is used (without position).
   */
  @Test
  public void nullElementWhenMessageAndNoPosition() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("There are null elements");

    checkNotNullElements(ONE_ELEMENT_NULL, "There are null elements");
  }

  /**
   * Verify that an NPE is thrown and a specified message is used (with position).
   */
  @Test
  public void nullElementWhenMessageAndPosition() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("There is a null element at position '1'");

    checkNotNullElements(ONE_ELEMENT_NULL, "There is a null element at position '%s'");
  }

  /**
   * Verify that an NPE is thrown and a specified message is used (without position).
   */
  @Test
  public void nullElementWhenMessageWithArgumentsAndNoPosition() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("There are null elements here");

    checkNotNullElements(ONE_ELEMENT_NULL, "There are null elements %s", "here");
  }

  /**
   * Verify that an NPE is thrown and a specified message is used (with position).
   */
  @Test
  public void nullElementWhenMessageWithArgumentsAndPosition() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("There are null elements here at position '1'");

    checkNotNullElements(ONE_ELEMENT_NULL, "There are null elements %s at position '%s'", "here");
  }

  /**
   * Verify that validation passes when there are no null elements.
   */
  @Test
  public void noNullElement() {
    checkNotNullElements(ALL_ELEMENTS_VALID);
  }

  /**
   * Verify that validation passes when there are no null elements.
   */
  @Test
  public void noNullElementWithMessage() {
    checkNotNullElements(ALL_ELEMENTS_VALID, "There are null elements");
  }

  /**
   * Verify that validation passes when there are no null elements.
   */
  @Test
  public void noNullElementWithMessageAndArguments() {
    checkNotNullElements(ALL_ELEMENTS_VALID, "There are null elements %s", "here");
  }

}
