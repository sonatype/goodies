/*
 * Copyright (c) 2011-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.goodies.common.io;

import java.io.PrintWriter;

/**
 * String-based {@link PrintWriter} backed by {@link StringBuilderWriter}.
 *
 * @since 1.0
 */
public class PrintBuffer
    extends PrintWriter
{
  public PrintBuffer() {
    super(new StringBuilderWriter(), true);
  }

  public StringBuilder getBuffer() {
    return ((StringBuilderWriter) out).getBuffer();
  }

  /**
   * @since 1.1
   */
  public PrintBuffer formatln(final String format, final Object... args) {
    format(format, args);
    println();
    return this;
  }

  public void reset() {
    getBuffer().setLength(0);
  }

  @Override
  public String toString() {
    return getBuffer().toString();
  }
}