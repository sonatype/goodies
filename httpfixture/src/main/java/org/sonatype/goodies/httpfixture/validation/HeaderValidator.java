/*
 * Copyright (c) 2010-present Sonatype, Inc. All rights reserved.
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
package org.sonatype.goodies.httpfixture.validation;

import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertTrue;

/**
 * Validator for http headers.
 *
 * @since 2.2.0
 */
public class HeaderValidator
    implements Validator
{
  private static final String EXPECTED_USER_AGENT_REGEX = String.format(
      "^Nexus/[0-9\\.-]+ \\((OSS|PRO){1}; %s; %s; %s; %s\\).*",
      System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"),
      System.getProperty("java.version"));

  /**
   * Map of header names to expected header regex.
   */
  private Map<String, String> expectedHeaders = ImmutableMap.of(HttpHeaders.USER_AGENT, EXPECTED_USER_AGENT_REGEX);

  private int successCount = 0;

  @Override
  public void validate(Object... validationObjects) {
    checkState(validationObjects.length > 0, "No input to validate.");

    for (Object obj : validationObjects) {
      validateHeaders(obj);
      successCount++;
    }
  }

  private void validateHeaders(Object obj) {
    if (obj instanceof io.netty.handler.codec.http.HttpRequest) {
      expectedHeaders.forEach((k, v) -> {
        String header = ((io.netty.handler.codec.http.HttpRequest) obj).headers().get(k);
        validateHeader(header, v);
      });
    }
    else if (obj instanceof javax.servlet.http.HttpServletRequest) {
      expectedHeaders.forEach((k, v) -> {
        String header = ((javax.servlet.http.HttpServletRequest) obj).getHeader(k);
        validateHeader(header, v);
      });
    }
    else {
      throw new IllegalStateException(
          String.format("Validation for given type '%s' has not been implemented.", obj.getClass()));
    }
  }

  private void validateHeader(String header, String regex) {
    assertTrue(String.format("Header value '%s' must match regex '%s'.", header, regex),
        Pattern.matches(regex, header));
  }

  @Override
  public int getSuccessCount() {
    return successCount;
  }

  @Override
  public void resetSuccessCount() {
    successCount = 0;
  }

}
