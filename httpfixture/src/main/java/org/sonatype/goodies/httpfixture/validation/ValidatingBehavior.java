/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.goodies.httpfixture.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.goodies.httpfixture.server.api.Behaviour;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link Behavior} with support for validation. Note, in order for this to work
 * with multiple behaviors, this one must be set first within the
 * {@link Server#withBehaviors()} method.
 *
 * @since 2.2.0
 */
public class ValidatingBehavior
    implements Behaviour
{

  private static final Logger log = LoggerFactory.getLogger(ValidatingBehavior.class);

  private final List<HttpValidator> validators;

  private int successCount = 0;

  public ValidatingBehavior(HttpValidator... validators) {
    checkArgument(validators != null && validators.length > 0, "Must have at least one validator set.");
    this.validators = Arrays.asList(validators);
  }

  @Override
  public boolean execute(HttpServletRequest request, HttpServletResponse response, Map<Object, Object> ctx)
      throws Exception
  {
    log.info("Executing validating behavior on '{}' request.", request.getMethod());
    for (HttpValidator v : validators) {
      v.validate(request);
    }
    successCount++;
    return true;
  }

  public int getSuccessCount() {
    return successCount;
  }

  public void resetSuccessCount() {
    successCount = 0;
  }

}
