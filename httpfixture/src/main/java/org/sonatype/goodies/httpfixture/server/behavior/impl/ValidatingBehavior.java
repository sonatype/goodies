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
package org.sonatype.goodies.httpfixture.server.behavior.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.goodies.httpfixture.server.api.Behaviour;
import org.sonatype.goodies.httpfixture.validation.Validator;

import static com.google.common.base.Preconditions.checkState;

/**
 * {@link Behavior} with support for validation.
 *
 * @since 2.2.0
 */
public class ValidatingBehavior
    implements Behaviour
{

  private Validator[] validators;

  public ValidatingBehavior withValidators(Validator... validators) {
    verify(validators);
    this.validators = validators;
    return this;
  }

  @Override
  public boolean execute(HttpServletRequest request, HttpServletResponse response, Map<Object, Object> ctx)
      throws Exception
  {
    verify(validators);
    for (Validator v : validators) {
      v.validate(request);
    }
    return true;
  }

  private void verify(Validator... validators) {
    checkState(validators != null && validators.length > 0, "Must have at least one validator set.");
  }

}
