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
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.goodies.httpfixture.server.jetty.behaviour.BehaviourSupport;

/**
 * {@link Behavior} with support for validation. Note, in order for this to work
 * with multiple behaviors, this one must be set first within the
 * {@link Server#withBehaviors()} method.
 *
 * @since 2.2.0
 */
public class ValidatingBehavior
    extends BehaviourSupport
{

  private final HttpValidator[] validators;

  private AtomicInteger successCount = new AtomicInteger();

  public ValidatingBehavior(HttpValidator... validators) {
    ValidationUtil.verifyValidators(validators);

    this.validators = validators;
  }

  @Override
  public boolean execute(HttpServletRequest request, HttpServletResponse response, Map<Object, Object> ctx)
      throws Exception
  {
    log.info("Executing validating behavior on '{}' request.", request.getMethod());
    for (HttpValidator v : validators) {
      v.validate(request);
    }
    successCount.incrementAndGet();
    return true;
  }

  public int getSuccessCount() {
    return successCount.get();
  }

  public void resetSuccessCount() {
    successCount.set(0);
  }

}
