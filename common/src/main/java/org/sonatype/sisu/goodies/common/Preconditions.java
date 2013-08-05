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

import javax.annotation.Nullable;

/**
 * Additional guava like Preconditions.
 *
 * @since 1.4
 */
public abstract class Preconditions
{
  /**
   * @since 1.5
   */
  private Preconditions() {}

  /**
   * Ensures that elements of an {@link Iterable} are not null. If reference is null by itself, no validation is
   * performed.
   *
   * @param reference an iterable reference
   * @return the reference that was validated
   * @throws NullPointerException if {@code reference} has null elements
   */
  public static <T> Iterable<T> checkNotNullElements(final Iterable<T> reference) {
    return checkNotNullElements(reference, "Element at position '%s'");
  }

  /**
   * Ensures that elements of an {@link Iterable} are not null. If reference is null by itself, no validation is
   * performed.
   *
   * @param reference    an object reference
   * @param errorMessage the exception message to use if the check fails; will be converted to a string using
   *                     {@link String#valueOf(Object)}. Message can contain a {@code %s} placeholder to be replaced
   *                     with position of {@code null} element
   * @return the reference that was validated
   * @throws NullPointerException if {@code reference} has null elements
   */
  public static <T> Iterable<T> checkNotNullElements(final Iterable<T> reference,
                                                     final @Nullable Object errorMessage)
  {
    if (reference != null && reference.iterator().hasNext()) {
      int i = 0;
      for (T element : reference) {
        if (element == null) {
          String msg = String.valueOf(errorMessage);
          if (msg != null && msg.contains("%s")) {
            msg = SimpleFormat.format(msg, i);
          }
          throw new NullPointerException(msg);
        }
        i++;
      }
    }
    return reference;
  }

  /**
   * Ensures that elements of an {@link Iterable} are not null. If reference is null by itself, no validation is
   * performed.
   *
   * @param reference            an iterable reference
   * @param errorMessageTemplate a template for the exception message should the check fail. The message is formed by
   *                             replacing each {@code %s} placeholder in the template with an argument. These are
   *                             matched by position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
   *                             Unmatched arguments will be appended to the formatted message in square braces.
   *                             Unmatched placeholders will be left as-is. If after this message contains an
   *                             {@code %s} placeholder it will be replaced with position of {@code null} element.
   * @param errorMessageArgs     the arguments to be substituted into the message
   *                             template. Arguments are converted to strings using
   *                             {@link String#valueOf(Object)}.
   * @return the reference that was validated
   * @throws NullPointerException if {@code reference} has null elements
   */
  public static <T> Iterable<T> checkNotNullElements(final Iterable<T> reference,
                                                     final @Nullable String errorMessageTemplate,
                                                     final @Nullable Object... errorMessageArgs)
  {
    if (reference != null && reference.iterator().hasNext()) {
      int i = 0;
      for (T element : reference) {
        if (element == null) {
          String msg = errorMessageTemplate;
          if (msg != null) {
            msg = SimpleFormat.format(msg, errorMessageArgs);
            if (msg != null && msg.contains("%s")) {
              msg = SimpleFormat.format(msg, i);
            }
          }
          throw new NullPointerException(msg);
        }
        i++;
      }
    }
    return reference;
  }
}
