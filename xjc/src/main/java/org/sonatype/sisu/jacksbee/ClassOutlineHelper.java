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
package org.sonatype.sisu.jacksbee;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 * Stateful helper for {@link ClassOutline} operations.
 *
 * @since jacksbee 1.0
 */
public class ClassOutlineHelper
{
  private static final String ACCESSOR_PREFIX_GET = "get";

  private static final String ACCESSOR_PREFIX_IS = "is";

  private final Map<String, JMethod> methodMap;

  /**
   * @param outline the ClassOutline to perform operations on
   */
  public ClassOutlineHelper(final ClassOutline outline) {
    methodMap = generateMethodIndex(outline);

  }

  /**
   * Finds 'get' and 'is' accessor methods for the specified field.
   *
   * @return the accessor method matching the field or null if none
   */
  public JMethod findAccessor(final FieldOutline field) {
    // Get the public name for the field.
    String name = field.getPropertyInfo().getName(true);

    // Look for a 'get' then 'is' accessors.
    JMethod accessorMethod = methodMap.get(ACCESSOR_PREFIX_GET + name);
    if (accessorMethod == null) {
      accessorMethod = methodMap.get(ACCESSOR_PREFIX_IS + name);
    }
    return accessorMethod;
  }

  private Map<String, JMethod> generateMethodIndex(final ClassOutline outline) {
    Map<String, JMethod> map = new HashMap<String, JMethod>();
    for (JMethod classMethod : outline.implClass.methods()) {
      map.put(classMethod.name(), classMethod);
    }
    return map;
  }
}
