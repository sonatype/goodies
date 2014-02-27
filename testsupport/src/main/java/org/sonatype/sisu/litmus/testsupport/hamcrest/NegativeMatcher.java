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

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A matcher that is able to describe itself when inverted.
 *
 * @since 1.4
 */
public interface NegativeMatcher<T>
    extends Matcher<T>
{

  /**
   * Generates a description of the object in case that matcher is inverted (negated). The description may be part of
   * a description of a larger object of which this is just a component, so it should be worded appropriately.
   *
   * @param description to be built or appended to
   */
  void describeNegationTo(final Description description);

}
