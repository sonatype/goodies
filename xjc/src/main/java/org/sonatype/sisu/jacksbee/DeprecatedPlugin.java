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

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;

/**
 * Adds {@link Deprecated} to generated types.
 *
 * @since jacksbee 1.0
 */
public class DeprecatedPlugin
    extends AbstractParameterizablePlugin
{
  @Override
  public String getOptionName() {
    return "Xdeprecated";
  }

  @Override
  public String getUsage() {
    return "Adds @Deprecated to generated types.";
  }

  @Override
  protected boolean run(final Outline outline, final Options options) throws Exception {
    assert outline != null;
    assert options != null;

    for (ClassOutline type : outline.getClasses()) {
      addGenerated(type.implClass);
    }

    for (EnumOutline type : outline.getEnums()) {
      addGenerated(type.clazz);
    }

    return true;
  }

  private void addGenerated(final JDefinedClass type) {
    assert type != null;
    JAnnotationUse anno = type.annotate(Deprecated.class);
  }
}