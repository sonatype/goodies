/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.goodies.template;

import com.google.common.base.Throwables;
import org.apache.commons.lang.StringEscapeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to deal with {@link Throwable} instances in a template.
 *
 * @since 1.4
 */
@TemplateAccessible
public class TemplateThrowableAdapter
{
  private final Throwable cause;

  public TemplateThrowableAdapter(final Throwable cause) {
    this.cause = checkNotNull(cause);
  }

  public Throwable getCause() {
    return cause;
  }

  public String getType() {
    return cause.getClass().getName();
  }

  public String getSimpleType() {
    return cause.getClass().getSimpleName();
  }

  public String getMessage() {
    return StringEscapeUtils.escapeHtml(cause.getMessage());
  }

  public String getTrace() {
    return StringEscapeUtils.escapeHtml(Throwables.getStackTraceAsString(cause));
  }

  public String toString() {
    return cause.toString();
  }
}
