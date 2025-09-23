/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.goodies.i18n;

import java.util.Arrays;
import java.util.List;

import org.sonatype.goodies.common.ComponentSupport;

import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A message source which aggregates messages sources in order.
 *
 * @since 1.0
 */
public class AggregateMessageSource
    extends ComponentSupport
    implements MessageSource
{
  private final List<MessageSource> sources = Lists.newArrayList();

  public AggregateMessageSource(final List<MessageSource> sources) {
    checkNotNull(sources);
    this.sources.addAll(sources);
  }

  public AggregateMessageSource(final MessageSource... sources) {
    this(Arrays.asList(sources));
  }

  public List<MessageSource> getSources() {
    return sources;
  }

  @Override
  public String getMessage(final String code) {
    String result = null;

    for (MessageSource source : sources) {
      try {
        result = source.getMessage(code);
        if (result != null) {
          break;
        }
      }
      catch (ResourceNotFoundException e) {
        log.trace(e.toString(), e);
      }
    }

    if (result == null) {
      throw new ResourceNotFoundException(code);
    }

    return result;
  }

  @Override
  public String getMessage(final String code, final String defaultValue) {
    try {
      return getMessage(code);
    }
    catch (ResourceNotFoundException e) {
      return defaultValue;
    }
  }

  @Override
  public String format(final String code, final Object... args) {
    String result = null;

    for (MessageSource source : sources) {
      try {
        result = source.format(code, args);
        if (result != null) {
          break;
        }
      }
      catch (ResourceNotFoundException e) {
        log.trace(e.toString(), e);
      }
    }

    if (result == null) {
      throw new ResourceNotFoundException(code);
    }

    return result;
  }
}