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
package org.sonatype.sisu.goodies.common;

/**
 * Support for format templates.
 *
 * @see SimpleFormat#template(String, Object...)
 * @since 1.3
 */
public abstract class FormatTemplate
{
  private final String format;

  private final Object[] args;

  private boolean dynamic = false;

  private String result;

  public FormatTemplate(final String format, final Object[] args) {
    this.format = format;
    this.args = args;
  }

  public String getFormat() {
    return format;
  }

  public Object[] getArgs() {
    return args;
  }

  public boolean isDynamic() {
    return dynamic;
  }

  public void setDynamic(final boolean dynamic) {
    this.dynamic = dynamic;
  }

  protected abstract String render();

  public String evaluate() {
    if (isDynamic()) {
      return render();
    }
    if (result == null) {
      result = render();
    }
    return result;
  }

  @Override
  public String toString() {
    return evaluate();
  }
}
