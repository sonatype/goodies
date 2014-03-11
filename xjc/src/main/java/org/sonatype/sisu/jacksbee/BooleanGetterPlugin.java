/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
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

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;

/**
 * Replaces any <tt>isXXX()</tt> method with <tt>getXXX()</tt> where the return type is {@link Boolean}.
 * Methods with primitive <tt>boolean</tt> are left as-is.
 *
 * @since jacksbee 1.0
 */
public class BooleanGetterPlugin
    extends AbstractParameterizablePlugin
{
  @Override
  public String getOptionName() {
    return "XbooleanGetter";
  }

  @Override
  public String getUsage() {
    return "Replaces isXXX() methods with getXXX() for getters of type java.lang.Boolean.";
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      for (JMethod method : type.implClass.methods()) {
        if (method.name().startsWith("is") && method.listParams().length == 0) {
          JType rtype = method.type();
          if (rtype.fullName().equals(Boolean.class.getName())) {
            method.name("get" + method.name().substring(2));
          }
        }
      }
    }

    return true;
  }
}