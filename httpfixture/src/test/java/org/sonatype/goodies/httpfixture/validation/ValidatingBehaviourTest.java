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

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidatingBehaviourTest
{

  @Captor
  private ArgumentCaptor<HttpServletRequest> requestCaptor1;

  @Captor
  private ArgumentCaptor<HttpServletRequest> requestCaptor2;

  @Test
  public void testExecuteAndSuccessCount() throws Exception {
    // Setup
    HttpValidator v1 = mock(HttpValidator.class);
    HttpValidator v2 = mock(HttpValidator.class);
    HttpValidator[] validators = new HttpValidator[]{v1, v2};
    HttpServletRequest request = mock(HttpServletRequest.class);

    ValidatingBehaviour underTest = new ValidatingBehaviour(validators);
    assertThat(underTest.getSuccessCount(), equalTo(0));

    // Execute
    boolean returned = underTest.execute(request, null, null);

    // Verify
    assertThat(returned, equalTo(true));

    verify(v1).validate(requestCaptor1.capture());
    assertThat(requestCaptor1.getValue(), equalTo(request));
    verify(v2).validate(requestCaptor2.capture());
    assertThat(requestCaptor2.getValue(), equalTo(request));

    assertThat(underTest.getSuccessCount(), equalTo(1));
  }
}
