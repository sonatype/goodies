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
package org.sonatype.goodies.i18n;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message source which prefixes message codes.
 *
 * @since 1.0
 */
public class PrefixingMessageSource
    implements MessageSource
{
  private final MessageSource messages;

  private final String prefix;

  public PrefixingMessageSource(final MessageSource messages, final String prefix) {
    this.messages = checkNotNull(messages);
    this.prefix = checkNotNull(prefix);
  }

  protected String createCode(final String code) {
    checkNotNull(code);
    return prefix + code;
  }

  @Override
  public String getMessage(final String code) {
    return messages.getMessage(createCode(code));
  }

  @Override
  public String getMessage(final String code, final String defaultValue) {
    return messages.getMessage(createCode(code), defaultValue);
  }

  @Override
  public String format(final String code, final Object... args) {
    return messages.format(createCode(code), args);
  }
}