/*
 * Copyright (c) 2007-2013 Sonatype, Inc. All rights reserved.
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

package org.sonatype.sisu.litmus.testsupport.mock;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues;
import org.mockito.invocation.InvocationOnMock;

/**
 * ???
 *
 * @since 1.0
 */
public class ImplementsPartialJavaBean
    extends ReturnsMoreEmptyValues
    implements Serializable
{
  private final Map<String, Object> methodToValueMap = Maps.newConcurrentMap();

  @Override
  public Object answer(final InvocationOnMock invocation) throws Throwable {
    Method method = invocation.getMethod();
    if (method.getName().startsWith("set") && (method.getParameterTypes().length == 1)) {
      if (invocation.getArguments()[0] != null) {
        methodToValueMap.put(method.getName().substring(3), invocation.getArguments()[0]);
      }
      else {
        methodToValueMap.remove(method.getName().substring(3));
      }
      return null;
    }
    else if (method.getName().startsWith("is") && (method.getParameterTypes().length == 0)) {
      Object retval = methodToValueMap.get(method.getName().substring(2));
      if (retval != null) {
        return retval;
      }
    }
    else if (method.getName().startsWith("get") && (method.getParameterTypes().length == 0)) {
      Object retval = methodToValueMap.get(method.getName().substring(3));
      if (retval != null) {
        return retval;
      }
    }

    return super.answer(invocation);
  }
}
